package org.ql.block.peer.communication.message.thread;

import lombok.Data;
import org.ql.block.peer.communication.message.thread.enums.ThreadMessageType;
import org.ql.block.peer.communication.message.thread.pojo.ThreadMessage;

/**
 * Created at 2022/11/15 13:42
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
public class ThreadMessageVO {
  private ThreadMessageType messageType;
  private ThreadMessage message;
  public ThreadMessageVO(ThreadMessage message,ThreadMessageType messageType) {
    this.messageType = messageType;
    this.message = message;
  }
}
