package org.ql.block.db.share.enums;


import lombok.extern.slf4j.Slf4j;

import java.util.EnumSet;

/**
 * Created at 2023/1/2 21:05
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Slf4j
public enum LimitEnum {
  LIMIT("limit"),WHERE("where");
  public final String limit;

  LimitEnum(String limit) {
    this.limit = limit;
  }


  public String toString() {
    return this.limit;
  }
  // 实现字符串转枚举的静态方法
  public static LimitEnum fromString(String limit) {
    if (limit == null) {
      return null;
    }
    return EnumSet.allOf(LimitEnum.class).stream()
            .filter(s -> s.toString().equals(limit))
            .findAny()
            .orElseThrow(() -> {
              log.error("Invalid limit: " + limit);
              return new IllegalArgumentException("Invalid limit: " + limit);
            });
  }
}
