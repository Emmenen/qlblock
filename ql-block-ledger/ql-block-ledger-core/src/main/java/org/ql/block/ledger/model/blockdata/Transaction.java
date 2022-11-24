package org.ql.block.ledger.model.blockdata;

import org.ql.block.ledger.model.blockchain.BlockChain;
import org.ql.block.common.exceptions.BalanceNotEnoughError;
import org.ql.block.ledger.util.ObjectUtil;

import java.io.Serializable;
import java.util.*;

/**
 * Created at 2022/7/13 23:47
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class Transaction implements Serializable{
  private static final long serialVersionUID = 111;
  public final int reward;
  public String id;
  public TXInput[] vIn;
  public TXOutput[] vOut;

  //todo 创建交易
  public Transaction(String from, String to, int amount, BlockChain blockChain, int reward) throws BalanceNotEnoughError {
    this.reward = reward;
  }
  public Transaction(int reward){
    this.reward = reward;
  }

  public boolean isBaseCoin(){
    boolean flag = false;
    if (null == vIn || vIn.length==0){
      flag = true;
    }
    return flag;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Transaction)) return false;

    Transaction that = (Transaction) o;

    if (reward != that.reward) return false;
    if (!Objects.equals(id, that.id)) return false;
    // Probably incorrect - comparing Object[] arrays with Arrays.equals
    if (!Arrays.equals(vIn, that.vIn)) return false;
    // Probably incorrect - comparing Object[] arrays with Arrays.equals
    return Arrays.equals(vOut, that.vOut);
  }

  @Override
  public int hashCode() {
    int result = reward;
    result = 31 * result + (id != null ? id.hashCode() : 0);
    result = 31 * result + Arrays.hashCode(vIn);
    result = 31 * result + Arrays.hashCode(vOut);
    return result;
  }

  /**
   * 创建一个coinBase交易
   * @param address
   * @param reward
   */
  public Transaction(String address, int reward){
    this.reward = reward;

    TXOutput txOutput = new TXOutput();
    txOutput.scriptPubKey = address;
    txOutput.value = this.reward;

    TXOutput[] txOutputs = new TXOutput[1];
    txOutputs[0] = txOutput;

    this.vOut = txOutputs;
    this.id = uid();
  }

  private String uid(){
    Date date = new Date();
    return String.valueOf(date.getTime());
  }

  public int getSize(){
    byte[] bytes = ObjectUtil.ObjectToByteArray(this);
    return bytes.length;
  }


}
