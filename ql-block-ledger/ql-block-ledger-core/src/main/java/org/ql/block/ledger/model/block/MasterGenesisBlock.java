package org.ql.block.ledger.model.block;

import org.ql.block.ledger.model.blockdata.BlockData;

/**
 * Created at 2022/11/17 21:21
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class MasterGenesisBlock extends MasterBlock{

  public MasterGenesisBlock(String previousHash, BlockData data) {
    super("genesis", data);
  }

  public void setHash(String hash){
    this.currentHash = hash;
  }

  @Override
  public void setHash() {
    setHash("000000A6FFE60F75B9493DF9C9F39EFCFF7F2CCE1F305BB34D4C615E1584156B");
  }
}
