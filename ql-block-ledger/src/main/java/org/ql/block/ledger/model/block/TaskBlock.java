package org.ql.block.ledger.model.block;


import org.ql.block.ledger.model.blockdata.BlockData;

/**
 * Created at 2022/6/29 20:01
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class TaskBlock extends Block {
  public byte[] publicKey;
  public byte[][] managerGroup;

  public TaskBlock(String previousHash, BlockData data) {
    super(previousHash, data);
  }

  @Override
  public void setHash() {
    super.setHash();
  }
}
