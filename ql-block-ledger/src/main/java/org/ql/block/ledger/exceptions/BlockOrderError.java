package org.ql.block.ledger.exceptions;

/**
 * Created at 2022/10/7 16:15
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class BlockOrderError extends AddBlockError{
  public BlockOrderError() {
  }

  public BlockOrderError(String message) {
    super(message);
  }

  public BlockOrderError(String message, Throwable cause) {
    super(message, cause);
  }

  public BlockOrderError(Throwable cause) {
    super(cause);
  }

  public BlockOrderError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
