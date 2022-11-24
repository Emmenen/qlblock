package org.ql.block.ledger.model.block;

import org.ql.block.ledger.model.blockchain.MasterBlockChain;
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
            ", timestamp=" + timestamp +
            ", miner='" + miner + '\'' +
            ", height=" + height +
            ", data=" + data +
            '}';
  }


  public MasterBlock(String previousHash, BlockData data) {
    super(previousHash, data);
    /**
     * hash计算完成之后，将奖励交易打包进区块
     */
    Transaction[] transactions = data.getTransactions();
    if (transactions == null){
      transactions = new Transaction[0];
    }
    Transaction[] transactionsReward = new Transaction[transactions.length + 1];
    for (int i = 0; i < transactionsReward.length - 1; i++) {
      transactionsReward[i+1] = transactions[i];
    }
    transactionsReward[0] = new Transaction(miner,50);
    BlockData blockData = new BlockData(transactionsReward);

    this.data = blockData;
    this.height = -1;
  }

  @Override
  public void setHash() {
    super.setHash();
  }

  @Override
  public boolean validate() {
    return super.validate();
  }
}
