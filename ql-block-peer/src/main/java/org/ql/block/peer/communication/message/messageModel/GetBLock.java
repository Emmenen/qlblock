package org.ql.block.peer.communication.message.messageModel;

/**
 * Created at 2022/10/7 14:33
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class GetBLock extends Message{
//  比特币是用过最顶层的hash计算高度的
//  private String lastHash;
  private Integer bestHeight;

  public Integer getBestHeight() {
    return bestHeight;
  }

  public GetBLock(Integer bestHeight) {
    this.bestHeight = bestHeight;
  }
}
