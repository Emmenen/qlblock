package org.ql.block.common.exceptions;

/**
 * Created at 2022/10/7 16:15
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class AddBlockError extends RuntimeException {
  public AddBlockError() {
  }

  public AddBlockError(String message) {
    super(message);
  }

  public AddBlockError(String message, Throwable cause) {
    super(message, cause);
  }

  public AddBlockError(Throwable cause) {
    super(cause);
  }

  public AddBlockError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
