package org.ql.block.peer.communication.message.peer.pojo;

import lombok.Data;
import org.ql.block.ledger.model.block.Block;

import java.io.Serializable;
import java.util.List;

/**
 * Created at 2022/10/7 14:34
 * Author: @Qi Long
 * email: 592918942@qq.com
 * 区块清单
 */
@Data
public class Inv implements PeerMessage, Serializable {
  private List<Block> blockList;
  public Inv(List<Block> blockList) {
    this.blockList = blockList;
  }


  @Override
  public long UID() {
    return hashCode()+System.currentTimeMillis();
  }
}
