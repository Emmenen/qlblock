package org.ql.block.ledger.model.block;

import org.ql.block.ledger.consensus.impl.PowOfWork;
import org.ql.block.ledger.consensus.impl.PowOfWorkForm;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.common.config.SpringContextUtil;
import org.ql.block.ledger.model.blockdata.Transaction;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created at 2022/6/29 19:55
 * Author: @Qi Long
 * email: 592918942@qq.com
 */

public abstract class Block implements Serializable {
  private static final long serialVersionUID = 111;
  public String previousHash;
  public String currentHash;
  /**
   * block的Data中可能存放参数，也可能存放交易记录。
   * 所以应该像个方法对数据格式化。以便之后浏览区块；
   */
  public BigInteger nonce;

  public Date timestamp;

  public String miner;

  public int height = -1;

  public BlockData data;

  public String getPreviousHash() {
    return new String(previousHash);
  }

  public String getCurrentHash() {
    return new String(currentHash);
  }

  public BlockData getData() {
    return data;
  }

  @Override
  public int hashCode(){
    return data.hashCode();
  }

  public Block() {
  }

  public Block(String previousHash, BlockData data) {
    this.previousHash = previousHash;
    this.data = data;
    this.setHash();
    this.timestamp = new Date();
  }

  public void setHash(){
    PowOfWork<PowOfWorkForm> powOfWork = SpringContextUtil.getBean("powOfWork",PowOfWork.class);
    PowOfWorkForm powOfWorkForm = powOfWork.run(this);
    String hash = powOfWorkForm.hash;
    BigInteger nonce = powOfWorkForm.nonce;
    this.currentHash = hash;
    this.nonce = nonce;
    this.timestamp = new Date();
    this.miner = powOfWorkForm.miner;
  }


  public boolean validate(){
    PowOfWork powOfWork = SpringContextUtil.getBean("powOfWork",PowOfWork.class);
    /**
     * 将包含在区块数据中的特殊交易取出，再进行hash验证
     */
    Transaction[] transactions = data.getTransactions();
    Transaction[] transactionsWithoutReward = new Transaction[transactions.length - 1];
    for (int i = 0; i < transactionsWithoutReward.length; i++) {
      transactionsWithoutReward[i] = transactions[i+1];
    }
    this.data.setTransactions(transactionsWithoutReward);
    boolean validate = powOfWork.validate(this);
    this.data.setTransactions(transactions);
    return validate;
  }

}
