package org.ql.block.ledger.model.blockchain;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.jetbrains.annotations.NotNull;
import org.ql.block.common.annotation.AddBlock;
import org.ql.block.ledger.exceptions.GetBlockError;
import org.ql.block.ledger.model.block.MasterBlock;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.model.blockdata.TXInput;
import org.ql.block.ledger.model.blockdata.TXOutput;
import org.ql.block.ledger.model.blockdata.Transaction;
import org.ql.block.ledger.db.Database;
import org.ql.block.ledger.exceptions.BalanceNotEnoughError;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;


/**
 * Created at 2022/7/9 0:14
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Repository
public class MasterBlockChain extends BlockChain{

  @Autowired
  public MasterBlockChain(Database staticDatabase) {
    super(staticDatabase);
    ChainName = "Master";
    this.init(staticDatabase);
  }

  public MasterBlock newGenesisBlock(){
    MasterBlock masterBlock = new MasterBlock("genesis", new BlockData("genesis"));
    return masterBlock;
  }


  @Override
  public LinkedList<Block> getBlocks(int offset, int number) throws GetBlockError {
    return super.getBlocks(offset,number);
  }

  @Override
  @AddBlock
  public void addBlock(@NotNull Block masterBlock) {
    if (masterBlock instanceof MasterBlock){
      super.addBlock(masterBlock);
    }else {
      try {
        throw new Exception("不是匹配的区块类型");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void findSpendableOutputs(){

  }



  @Override
  public DB getBlockDB() {
    return super.getBlockDB();
  }

  public int getBalance(String address){
    List<Transaction> unspentTransactions = findUnspentTransactions(address);
    int balance = 0;
    for (Transaction transaction : unspentTransactions) {
      balance += Arrays.stream(transaction.vOut).filter(vout -> vout.CanBeUnlockedWith(address)).mapToInt(vout -> vout.value).sum();
    }
    return balance;
  }

  //todo
  public List<Transaction> findUnspentTransactions(String address){
    /*
      1。遍历区块中所有的交易找到所有已经被消费的交易输出
      2。再次遍历区块中的交易，判断交易输出是否已经消费
     */
    HashMap<String, Integer> spendTXOut = new HashMap<>();
    List<Transaction> unSpendTX = new ArrayList<>();
    DB bucket = getBlockDB();
    bucket.forEach(k->{
      if (!Iq80DBFactory.asString(k.getKey()).equals("l")){
        Block block = (Block) ObjectUtil.byteArrayToObject(k.getValue());
        if (block.previousHash.equals("genesis")){
          return;
        }
        Transaction[] transactions = block.data.getTransactions();
        Arrays.stream(transactions).forEach(transaction -> {
          if (transaction.isBaseCoin()){
            return;
          }
          TXInput[] vIn = transaction.vIn;
          for (TXInput txInput : vIn) {
            //被交易输入引用过的交易即是消费过的交易。
            if (txInput.CanUnlockOutputWith(address)){
              spendTXOut.put(txInput.Txid,txInput.vOutIndex);
            }
          }
        });
      }
    });

    bucket.forEach(k->{
      if (!Iq80DBFactory.asString(k.getKey()).equals("l")){
        Block block = (Block) ObjectUtil.byteArrayToObject(k.getValue());
        if (block.previousHash.equals("genesis")){
          return;
        }
        Transaction[] transactions = block.data.getTransactions();
        Arrays.stream(transactions).forEach(transaction -> {
          if (spendTXOut.get(transaction.id)!=null){
            //说明此交易中当前地址对应的交易输出已经被消费了。
            return;
          }
          TXOutput[] vOut = transaction.vOut;
          for (TXOutput txOutput : vOut) {
            if (txOutput.CanBeUnlockedWith(address)) {
              unSpendTX.add(transaction);
            }
          }
        });
      }
    });
    return unSpendTX;
  }

  public Transaction createTx(String from, String to, int amount) throws BalanceNotEnoughError {
    List<Transaction> unspentTransactions = findUnspentTransactions(from);
    List<TXOutput> vOut = new ArrayList<>();
    List<TXInput> vIn = new ArrayList<>();
    int balance = 0;
    for (Transaction transaction : unspentTransactions) {
      TXOutput[] spendOut = transaction.vOut;
      TXInput txInput = new TXInput();
      txInput.Txid = transaction.id;
      txInput.script = from;
      for (int i = 0; i < spendOut.length; i++) {
        TXOutput vout = spendOut[i];
        if (vout.CanBeUnlockedWith(from)) {
          balance += vout.value;
          txInput.vOutIndex = i;
        }
      }
      vIn.add(txInput);
      if (balance>amount){
        break;
      }
    }
    if (balance<amount){
        throw new BalanceNotEnoughError("余额不足！");
    }
    if (balance!=amount){
      TXOutput outFrom = new TXOutput(balance-amount,from);
      vOut.add(outFrom);
    }
    TXOutput outTo = new TXOutput(amount,to);
    vOut.add(outTo);
    Transaction transaction = new Transaction();
    transaction.vOut = vOut.toArray(new TXOutput[0]);
    transaction.vIn = vIn.toArray(new TXInput[0]);
    long time = new Date().getTime();
    transaction.id = String.valueOf(time);
    return transaction;
  }


}
