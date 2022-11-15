package org.ql.block.peer.communication.message.peer.pojo;

import org.ql.block.ledger.model.block.Block;

import java.io.Serializable;

/**
 * Created at 2022/11/15 11:15
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class SetBlock implements PeerMessage, Serializable {
  private Block block;

  public SetBlock(Block block) {
    this.block = block;
  }

  public Block getBlock() {
    return block;
  }

  public void setBlock(Block block) {
    this.block = block;
  }

  @Override
  public long UID() {
    return block.hashCode()+block.timestamp.getTime();
  }
}
