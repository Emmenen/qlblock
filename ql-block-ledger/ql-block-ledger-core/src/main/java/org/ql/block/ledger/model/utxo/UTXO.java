package org.ql.block.ledger.model.utxo;

import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.ql.block.common.exceptions.BalanceNotEnoughError;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.model.blockchain.BlockChain;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.model.blockdata.TXInput;
import org.ql.block.ledger.model.blockdata.TXOutput;
import org.ql.block.ledger.model.blockdata.Transaction;
import org.ql.block.ledger.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.ql.block.ledger.config.LedgerConfig.utxoBucket;

/**
 * Created at 2022/10/26 9:30
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Component
@Slf4j
public class UTXO {

  @Autowired
  private BlockChain blockChain;


  public int getBalance(String address, BlockChain blockChain){
    TXOutput[] utxo = findUTXO(address);
    int balance = 0;
    for (TXOutput txOutput : utxo) {
      balance += txOutput.value;
    }
    return balance;
  }

  /**
   * 查询一个地址的所有可以消费的交易输出
   * @param address
   * @return txid -> 交易输出列表
   */
  public ConcurrentHashMap<String,UnSpentOutput[]> findSpendableOutputs(String address){
    ConcurrentHashMap<String, UnSpentOutput[]> spendableOutput = new ConcurrentHashMap<>();
    DB bucket = blockChain.dataBaseImpl.getBucket(utxoBucket);
    DBIterator iterator = bucket.iterator();
    while (iterator.hasNext()) {
      Map.Entry<byte[], byte[]> next = iterator.next();
      ArrayList<UnSpentOutput> utxos = new ArrayList<>();
      byte[] value = next.getValue();
      ArrayList<UnSpentOutput> outputList = ObjectUtil.byteArrayToObject(value, ArrayList.class);
      for (int i = 0; i < outputList.size(); i++) {
        if (outputList.get(i).CanBeUnlockedWith(address)) {
          utxos.add(outputList.get(i));
        }
      }
      spendableOutput.put(Iq80DBFactory.asString(next.getKey()),utxos.toArray(new UnSpentOutput[0]));
    }
    return spendableOutput;
  }
  /**
   * 查询一个地址的所有可以消费的交易输出
   * @param address
   * @return 交易输出列表
   */
  //找到指定address下的未花费交易
  public UnSpentOutput[] findUTXO(String address){
    ArrayList<UnSpentOutput> utxos = new ArrayList<>();
    DB bucket = blockChain.dataBaseImpl.getBucket(utxoBucket);
    DBIterator iterator = bucket.iterator();
    while (iterator.hasNext()) {
      Map.Entry<byte[], byte[]> next = iterator.next();
      byte[] value = next.getValue();
      ArrayList<UnSpentOutput> unSpentOutputs = ObjectUtil.byteArrayToObject(value, ArrayList.class);
      for (int i = 0; i < unSpentOutputs.size(); i++) {
        if (unSpentOutputs.get(i).CanBeUnlockedWith(address)) {
          utxos.add(unSpentOutputs.get(i));
        }
      }
    }
    return utxos.toArray(new UnSpentOutput[0]);
  }


  public void setReindex(){
    ConcurrentHashMap<String, ArrayList<UnSpentOutput>> utxo = blockChain.FindUTXO();
    blockChain.dataBaseImpl.deleteBuket(utxoBucket);
    blockChain.dataBaseImpl.createBucket(utxoBucket);
    utxo.forEach((k,v)->{
        blockChain.dataBaseImpl.update(utxoBucket, bucket->{
          bucket.put(Iq80DBFactory.bytes(k),ObjectUtil.ObjectToByteArray(v));
        });
    });
  }

  public void update(Block block){
    //获取区块体
    BlockData data = block.getData();
    //获取区块体中的交易
    Transaction[] transactions = data.getTransactions();
    DB bucket = blockChain.dataBaseImpl.getBucket(utxoBucket);

    //遍历交易
    for (Transaction tx : transactions) {
      TXInput[] vIn = tx.vIn;
      if (!tx.isBaseCoin()){
        for (int i = 0; i < vIn.length; i++) {
          //取出交易中引用的交易ID,然后消费其中的交易输出
          ArrayList<TXOutput> voutIndex = ObjectUtil.byteArrayToObject(bucket.get(Iq80DBFactory.bytes(vIn[i].Txid)),ArrayList.class);

          for (int j = 0; j < voutIndex.size(); j++) {
           // 判断取出的交易中的交易输出是否被消费

            if (j == vIn[i].vOutIndex){
              //被消费了
              voutIndex.remove(j);
            }
          }

          //如果一个txid下的所有未消费的交易输出都被新的交易引用（消费）了，则从UTXO中删除该txid;
          if (voutIndex.size()==0){
            bucket.delete(Iq80DBFactory.bytes(vIn[i].Txid));
          }else {
            bucket.put(Iq80DBFactory.bytes(vIn[i].Txid),ObjectUtil.ObjectToByteArray(voutIndex));
          }
        }
      }

      //新块中的交易输出都是未消费的;
      TXOutput[] vOut = tx.vOut;
      ArrayList<Integer> unSpend = new ArrayList<>();
      for (int i = 0; i < vOut.length; i++) {
        unSpend.add(i);
      }
      bucket.put(Iq80DBFactory.bytes(tx.id),ObjectUtil.ObjectToByteArray(unSpend));
    }
  }

  public Transaction createTx(String from, String to, int amount,int fee) throws BalanceNotEnoughError {
//    List<Transaction> unspentTransactions = findUnspentTransactions(from);
    ConcurrentHashMap<String, UnSpentOutput[]> utxo = findSpendableOutputs(from);
    List<TXOutput> vOut = new ArrayList<>();
    List<TXInput> vIn = new ArrayList<>();
    AtomicInteger balance = new AtomicInteger();
    Enumeration<String> keys = utxo.keys();


    while (keys.hasMoreElements()) {
      String k = keys.nextElement();
      UnSpentOutput[] v = utxo.get(k);
      if(balance.intValue()<amount){
        for (int i = 0; i < v.length; i++) {
          UnSpentOutput out = v[i];
          if (out.CanBeUnlockedWith(from)){
            TXInput txInput = new TXInput();
            txInput.Txid = k;
            txInput.script = from;
            balance.addAndGet(out.value);
            txInput.vOutIndex = out.txOutputN;
            vIn.add(txInput);
            // 一个交易中同一个地址最多能有一条交易输出
            break;
          }
        }
      }else {
    //找到了足够的交易输出来支持本次交易;
        break;
      }
    }
    //遍历结束仍然没有足量的UTXO，提示余额不足;
    if (balance.get() <amount){
      log.warn("{}的余额不足，不足以发起此次交易",from);
      throw new BalanceNotEnoughError("余额不足！");
    }
    if (balance.get() !=amount){
      TXOutput outFrom = new TXOutput(balance.get() -amount,from);
      vOut.add(outFrom);
    }
    TXOutput outTo = new TXOutput(amount,to);
    vOut.add(outTo);
    Transaction transaction = new Transaction(fee);
    transaction.vOut = vOut.toArray(new TXOutput[0]);
    transaction.vIn = vIn.toArray(new TXInput[0]);
    long time = new Date().getTime();
    transaction.id = String.valueOf(time);
    return transaction;
  }
}
