package org.ql.block.db.share.enums;


import lombok.extern.slf4j.Slf4j;

import java.util.EnumSet;

/**
 * Created at 2022/12/18 15:09
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Slf4j
public enum CommandEnum {
  CREATE("create"),
  DROP("drop"),
  USE("use"),
  INSERT("insert"),
  DELETE("delete"),
  UPDATE("update"),
  SELECT("select"),
  COUNT("count");
  private final String operation;
  CommandEnum(String operation) {
    this.operation = operation;
  }

  @Override
  public String toString() {
    return this.operation;
  }
  // 实现字符串转枚举的静态方法
  public static CommandEnum fromString(String operation) {
    if (operation == null) {
      return null;
    }
    return EnumSet.allOf(CommandEnum.class).stream()
            .filter(s -> s.toString().equals(operation))
            .findAny()
            .orElseThrow(() -> {
              log.error("Invalid option: " + operation);
              return new IllegalArgumentException("Invalid option: " + operation);
            });
  }

}
