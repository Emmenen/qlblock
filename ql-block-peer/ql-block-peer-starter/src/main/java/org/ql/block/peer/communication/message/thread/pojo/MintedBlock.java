package org.ql.block.peer.communication.message.thread.pojo;

import lombok.Data;
import org.ql.block.ledger.model.block.Block;

/**
 * Created at 2022/11/15 13:40
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
public class MintedBlock implements ThreadMessage{
  private Block block;

  public MintedBlock(Block block) {
    this.block = block;
  }
}
