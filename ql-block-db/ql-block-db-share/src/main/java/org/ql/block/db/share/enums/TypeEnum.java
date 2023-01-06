package org.ql.block.db.share.enums;

import lombok.extern.slf4j.Slf4j;

import java.util.EnumSet;
import java.util.Locale;

/**
 * Created at 2022/12/24 12:57
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Slf4j
public enum TypeEnum {
    DATABASE("database"),
    BUCKET("bucket");
    private final String type;

    TypeEnum(String type) {
      this.type = type;
    }

  @Override
  public String toString() {
    return type;
  }

  public static TypeEnum fromString(String type) {
      if (type == null) {
        return null;
      }
      String lowerCase = type.trim().toLowerCase(Locale.ROOT);
      return EnumSet.allOf(TypeEnum.class).stream()
              .filter(s -> s.toString().equals(lowerCase))
              .findAny()
              .orElseThrow(() ->{
                log.error("Invalid type: " + lowerCase);
                return new IllegalArgumentException("Invalid type: " + lowerCase);
              });
    }
}
