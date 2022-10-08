package org.ql.block.ledger.util;


import java.util.Arrays;

/**
 * Created at 2022/10/8 13:33
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class Base58 {
  private static Base58$ base58 = new Base58$();

  public static String encode(String str){
    return encode(str.getBytes());
  }

  public static String encode(byte[] bytes){
    return base58.encode(bytes);
  }

  public static String decode(String strHex){
    return new String(decodeToByte(strHex));
  }

  public static byte[] decodeToByte(String strHex){
    return base58.decode(strHex);
  }

  public static void main(String[] args) {
    System.out.println(encode("123"));
  }


}
