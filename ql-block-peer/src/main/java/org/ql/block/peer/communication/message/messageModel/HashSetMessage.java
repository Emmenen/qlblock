package org.ql.block.peer.communication.message.messageModel;

import lombok.Data;
import org.ql.block.peer.communication.message.messageModel.Message;
import org.ql.block.peer.model.Peer;

import java.util.HashSet;

/**
 * Created at 2022/10/6 20:35
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
public class HashSetMessage extends Message {
  private HashSet<Peer> addrYou;

  public HashSetMessage(HashSet<Peer> addrYou) {
    this.addrYou = addrYou;
  }
}
