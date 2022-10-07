package org.ql.block.ledger.util;

/**
 * Created at 2022/7/12 0:35
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class ArrayUtils {
  public static byte[] byteArraySum(byte[] bytesA, byte[] bytesB){
    byte[] bytes = new byte[bytesA.length + bytesB.length];
    System.arraycopy(bytesA,0,bytes,0,bytesA.length);
    System.arraycopy(bytesB,0,bytes,bytesA.length,bytesB.length);
    return bytes;
  }
}
