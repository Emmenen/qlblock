package org.ql.block.ledger.exceptions;

/**
 * Created at 2022/10/8 13:55
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class WalletInformationError extends Exception{
  public WalletInformationError() {
  }

  public WalletInformationError(String message) {
    super(message);
  }

  public WalletInformationError(String message, Throwable cause) {
    super(message, cause);
  }

  public WalletInformationError(Throwable cause) {
    super(cause);
  }

  public WalletInformationError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
