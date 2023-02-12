package org.ql.block.ledger.model.blockdata;

import org.ql.block.ledger.util.CryptoUtils;

import java.io.Serializable;
import java.security.DigestException;
import java.util.Arrays;

/**
 * Created at 2022/7/14 1:45
 * Author: @Qi Long
 * email: 592918942@qq.com
 * 所有存放到区块中的数据都应该用BlockData封装
 */
public class BlockData implements Serializable {
  private static final long serialVersionUID = 111;

  private Transaction[] transactions;
  private String textData;

  public BlockData(Transaction[] transactions) {
    this.transactions = transactions;
  }


  private byte[] getByteData(){
    /**
     * 这里应该是用Mekle树，暂时未实现。
     */
    StringBuilder sb = new StringBuilder();
    try {
      for (int i = 0; i < transactions.length; i++) {
        sb.append(transactions[i].id);
      }
    }catch (NullPointerException e){

    }
    byte[] bytes = sb.append(textData).toString().getBytes();
    String symbol = "qlBlock";
    if (bytes.length<32){
      int i = (32 - bytes.length) / 7 + 1;
      for (int j = 0; j < i; j++) {
        sb.append(symbol);
      }
    }
    return sb.append(textData).toString().getBytes();

  }
  /**
   * todo
   * sha256报错byte的长度至少是32
   * 获取BlockData中数据的hash;
   * @return
   */
  @Override
  public int hashCode(){
    byte[] byteData = getByteData();
    int digest = 0;
    try {
      digest = CryptoUtils.sha256.digest(byteData, 0, byteData.length);
    } catch (DigestException e) {
      e.printStackTrace();
    }
    if (digest==0){
      digest = super.hashCode();
    }
    return digest;
  }

  public BlockData(String textData) {
    this.textData = textData;
  }

  public Transaction[] getTransactions() {
    return transactions;
  }

  public void setTransactions(Transaction[] transactions) {
    this.transactions = transactions;
  }

  public String getTextData() {
    return textData;
  }

  @Override
  public String toString() {
    return "BlockData{" +
            "transactions=" + Arrays.toString(transactions) +
            ", textData='" + textData + '\'' +
            '}';
  }
}
