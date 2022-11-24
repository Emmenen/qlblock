package org.ql.block.common.exceptions;

/**
 * Created at 2022/10/7 15:00
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class GetBlockError extends Exception{
  public GetBlockError() {
  }

  public GetBlockError(String message) {
    super(message);
  }

  public GetBlockError(String message, Throwable cause) {
    super(message, cause);
  }

  public GetBlockError(Throwable cause) {
    super(cause);
  }

  public GetBlockError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
