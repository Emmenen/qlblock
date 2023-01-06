package org.ql.block.ledger.communication.wallet.message;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created at 2022/12/9 13:47
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
@NoArgsConstructor
public class BeConnected implements WalletMessage{
  private String msg;

  public BeConnected(String msg) {
    this.msg = msg;
  }
}
