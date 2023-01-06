package org.ql.block.db.share.enums;

/**
 * Created at 2022/12/24 16:44
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public enum STATUS {
  SUCCESS(200);

  STATUS(int status) {
    this.status = status;
  }

  public final int status;
}
