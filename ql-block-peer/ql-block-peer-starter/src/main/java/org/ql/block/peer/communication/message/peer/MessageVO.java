package org.ql.block.peer.communication.message.peer;

import lombok.Data;
import org.ql.block.peer.communication.message.peer.enums.MessageType;
import org.ql.block.peer.communication.message.peer.pojo.PeerMessage;

import java.io.Serializable;

/**
 * Created at 2022/10/6 20:23
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
public class MessageVO implements Serializable {
  private MessageType messageType;
  private PeerMessage message;
  public MessageVO(MessageType messageType, PeerMessage message) {
    this.messageType = messageType;
    this.message = message;
  }


}
