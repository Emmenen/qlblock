package org.ql.block.peer.communication.message;

import lombok.Data;

import java.io.Serializable;

/**
 * Created at 2022/10/6 20:23
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
public class MessageVO implements Serializable {
  private MessageType messageType;
  private Message message;

  public MessageVO(MessageType messageType, Message message) {
    this.messageType = messageType;
    this.message = message;
  }

}
