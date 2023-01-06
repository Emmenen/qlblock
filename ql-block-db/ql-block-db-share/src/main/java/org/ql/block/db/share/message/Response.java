package org.ql.block.db.share.message;

import lombok.Data;

import java.util.Collection;
import java.util.List;

/**
 * Created at 2022/12/18 15:01
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
public class Response<T> {
  private int status;
  private String message;
  private Collection<T> data;
}
