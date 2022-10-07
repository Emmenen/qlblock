package org.ql.block.ledger.model.blockdata;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.ledger.model.blockchain.BlockChain;
import org.ql.block.ledger.exceptions.BalanceNotEnoughError;

import java.io.Serializable;
import java.util.Date;

/**
 * Created at 2022/7/13 23:47
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class Transaction implements Serializable {
  private static final long serialVersionUID = 111;
  private static final int reward = 4;
  public String id;
  public TXInput[] vIn;
  public TXOutput[] vOut;

  //todo 创建交易
  public Transaction (String from, String to, int amount, BlockChain blockChain) throws BalanceNotEnoughError {
  }
  public Transaction (){
  }

  public boolean isBaseCoin(){
    boolean flag = false;
    if (null == vIn || vIn.length==0){
      flag = true;
    }
    return flag;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * 创建一个coinBase交易
   * @param address
   */
  public Transaction(String address){
    Transaction transaction = new Transaction();

    TXOutput txOutput = new TXOutput();
    txOutput.scriptPubKey = address;
    txOutput.value = reward;

    TXOutput[] txOutputs = new TXOutput[1];
    txOutputs[0] = txOutput;

    transaction.vOut = txOutputs;
    transaction.id = uid();
  }

  private String uid(){
    Date date = new Date();
    return String.valueOf(date.getTime());
  }
}
