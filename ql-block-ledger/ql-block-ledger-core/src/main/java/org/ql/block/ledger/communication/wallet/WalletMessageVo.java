package org.ql.block.ledger.communication.wallet;

import lombok.Data;
import org.ql.block.ledger.communication.wallet.enums.WalletType;
import org.ql.block.ledger.communication.wallet.message.WalletMessage;

/**
 * Created at 2022/12/9 13:43
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
public class WalletMessageVo {
  private WalletType walletType;
  private WalletMessage message;

  public WalletMessageVo(WalletType walletType, WalletMessage message) {
    this.walletType = walletType;
    this.message = message;
  }
}
