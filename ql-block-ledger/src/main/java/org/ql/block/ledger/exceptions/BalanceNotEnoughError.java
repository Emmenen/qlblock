package org.ql.block.ledger.exceptions;

/**
 * Created at 2022/7/14 0:48
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class BalanceNotEnoughError extends Exception{
  public BalanceNotEnoughError() {
    super();
  }

  public BalanceNotEnoughError(String message) {
    super(message);
  }
}
