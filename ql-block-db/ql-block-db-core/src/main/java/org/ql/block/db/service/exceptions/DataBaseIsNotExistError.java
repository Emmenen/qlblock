package org.ql.block.db.service.exceptions;

/**
 * Created at 2022/7/14 0:48
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class DataBaseIsNotExistError extends Exception{
  public DataBaseIsNotExistError() {
    super();
  }

  public DataBaseIsNotExistError(String message) {
    super(message);
  }
}