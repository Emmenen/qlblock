package org.ql.block.db.share.utils;


import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created at 2023/1/6 21:42
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class Iq80Factory {
  public static byte[] bytes(String value)
  {
    return (value == null) ? null : value.getBytes(UTF_8);
  }

  public static String asString(byte[] value)
  {
    return (value == null) ? null : new String(value, UTF_8);
  }
}
