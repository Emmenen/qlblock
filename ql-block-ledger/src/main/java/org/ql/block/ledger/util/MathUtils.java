package org.ql.block.ledger.util;

/**
 * Created at 2022/10/5 20:29
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class MathUtils {
  /**
   * byte[]转int
   * @param bytes
   * @return
   */
  public static int byteArrayToInt(byte[] bytes) {
    if (null == bytes||bytes.length==0)
      return 0;
    int value = 0;
    // 由高位到低位
    for (int i = 0; i < 4; i++) {
      int shift = (4 - 1 - i) * 8;
      value += (bytes[i] & 0x000000FF) << shift;// 往高位游
    }
    return value;
  }

  public static byte[] intToByteArray(int i) {
    byte[] result = new byte[4];
    // 由高位到低位
    result[0] = (byte) ((i >> 24) & 0xFF);
    result[1] = (byte) ((i >> 16) & 0xFF);
    result[2] = (byte) ((i >> 8) & 0xFF);
    result[3] = (byte) (i & 0xFF);
    return result;
  }

  public static void main(String[] args) {
    byte[] bytes = intToByteArray(100);
    int i = byteArrayToInt(bytes);
  }
}
