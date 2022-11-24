package org.ql.block.peer.communication.message.peer.pojo;

import lombok.Data;
import org.ql.block.common.beans.pojo.Peer;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created at 2022/10/6 20:35
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
public class HashSetMessage implements PeerMessage, Serializable {
  private HashSet<Peer> addrYou;

  public HashSetMessage(HashSet<Peer> addrYou) {
    this.addrYou = addrYou;
  }

  @Override
  public long UID() {
    return addrYou.hashCode()+System.currentTimeMillis();
  }
}
