package org.ql.block.db.share.message;

import lombok.Data;

/**
 * Created at 2023/1/2 21:41
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
public class Limit{
  private Integer offset = -1;
  private Integer size = -1;
}