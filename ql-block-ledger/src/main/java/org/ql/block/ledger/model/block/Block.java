package org.ql.block.ledger.model.block;

import org.ql.block.ledger.consensus.PowOfWork;
import org.ql.block.ledger.consensus.PowOfWorkForm;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.config.SpringContextUtil;

import java.io.Serializable;
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
  public int nonce;
  public Date timestamp;
  public String miner;
  //  todo 区块高度的计算
  public int height;

  public BlockData data;

  public String getPreviousHash() {
    return new String(previousHash);
  }

  public String getCurrentHash() {
    return new String(currentHash);
  }

  @Override
  public int hashCode(){
    return data.hashCode();
  }

  public Block(String previousHash, BlockData data) {
    this.previousHash = previousHash;
    this.timestamp = new Date();
    this.data = data;
    this.setHash();
  }

  public void setHash(){
    PowOfWork powOfWork = SpringContextUtil.getBean("powOfWork",PowOfWork.class);
    PowOfWorkForm powOfWorkForm = powOfWork.run(this);
    String hash = powOfWorkForm.hash;
    int nonce = powOfWorkForm.nonce;
    this.currentHash = hash;
    this.nonce = nonce;
    this.miner = powOfWorkForm.miner;
  }


  public boolean validate(){
    PowOfWork powOfWork = new PowOfWork();
    return powOfWork.validate(this);
  }

}
