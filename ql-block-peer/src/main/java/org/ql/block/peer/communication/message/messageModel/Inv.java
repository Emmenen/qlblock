package org.ql.block.peer.communication.message.messageModel;

import lombok.Data;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.peer.model.Peer;

import java.util.HashSet;
import java.util.List;

/**
 * Created at 2022/10/7 14:34
 * Author: @Qi Long
 * email: 592918942@qq.com
 * 区块清单
 */
@Data
public class Inv extends Message {
  private List<Block> blockList;
  public Inv(List<Block> blockList) {
    this.blockList = blockList;
  }


}
