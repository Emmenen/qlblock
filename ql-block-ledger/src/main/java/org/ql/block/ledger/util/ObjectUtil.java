package org.ql.block.ledger.util;

import java.io.*;

/**
 * Created at 2022/7/7 12:49
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class ObjectUtil<T> {

  public static Object byteArrayToObject(byte[] object){
    ByteArrayInputStream bin = new ByteArrayInputStream(object);
    return inputSteamToObject(bin);
  }
  public static Object inputSteamToObject(InputStream inputStream){
    try {
      ObjectInputStream oin = new ObjectInputStream(inputStream);
      Object o = oin.readObject();
      return o;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static byte[] ObjectToByteArray(Object object) {
    byte[] bytes;
    try {
      //字节数组输出流在内存中创建一个字节数组缓冲区，所有发送到输出流的数据保存在该字节数组缓冲区中。
      ByteArrayOutputStream bo = new ByteArrayOutputStream();
      ObjectOutputStream oo = new ObjectOutputStream(bo);
      oo.writeObject(object);
      bytes = bo.toByteArray();
      bo.close();
      oo.close();
      return bytes;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
