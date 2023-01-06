package org.ql.block.peer.communication.message.peer.pojo;

import lombok.Data;
import org.ql.block.ledger.communication.salve.SalveApply;

/**
 * Created at 2022/12/10 16:30
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
public class SalveApplyMessage implements PeerMessage{
  private SalveApply salveApply;

  @Override
  public long UID() {
    return salveApply.hashCode();
  }
}
