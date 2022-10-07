package org.ql.block.ledger.model.block;

import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.model.blockdata.Transaction;

/**
 * Created at 2022/6/29 20:01
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class MasterBlock extends Block {

  @Override
  public String toString() {
    return "MasterBlock{" +
            "previousHash='" + previousHash + '\'' +
            ", currentHash='" + currentHash + '\'' +
            ", nonce=" + nonce +
            ", data=" + data.getTextData() +
            '}';
  }



  public MasterBlock(String previousHash, BlockData data) {

    super(previousHash, data);
    if (data.getTransactions()==null){
      return;
    }
    Transaction[] transactions = data.getTransactions();
    Transaction[] transactionsReward = new Transaction[transactions.length + 1];
    for (int i = 0; i < transactionsReward.length - 1; i++) {
      transactionsReward[i+1] = transactions[i];
    }

    /**
     * 创建奖励给矿工的coinBase奖励
     */
    transactionsReward[0] = new Transaction(this.miner);
    BlockData blockData = new BlockData(transactionsReward);
    this.data = blockData;
  }

  @Override
  public void setHash() {
    super.setHash();
  }

  @Override
  public boolean validate() {
    /**
     * 将包含在区块数据中的特殊交易取出，再进行hash验证
     */
    Transaction[] transactions = data.getTransactions();
    Transaction[] transactionsWithoutReward = new Transaction[transactions.length - 1];
    for (int i = 0; i < transactionsWithoutReward.length; i++) {
      transactionsWithoutReward[i] = transactions[i+1];
    }
    this.data.setTransactions(transactionsWithoutReward);
    return super.validate();
  }
}
