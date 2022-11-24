package org.ql.block.ledger.util;

import com.alibaba.fastjson.JSON;

import java.io.*;

/**
 * Created at 2022/7/7 12:49
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class ObjectUtil<T> {

  public static Object byteArrayToObject(byte[] object) {
    ByteArrayInputStream bin = new ByteArrayInputStream(object);
    Object o = null;
    try {
      o = inputSteamToObject(bin);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return o;
  }
  public static Object inputSteamToObject(InputStream inputStream) throws IOException {

    ObjectInputStream oin = new ObjectInputStream(inputStream);
    Object o = null;
    try {
      o = oin.readObject();

    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return o;
  }
  public static  <T> T byteArrayToObject(byte[] object, Class<T> clazz) {
    return (T)byteArrayToObject(object);
  }


  public static byte[] ObjectToJsonByteArray(Object object){
    String s = JSON.toJSONString(object);
    return s.getBytes();
  }

  public static byte[] ObjectToByteArray(Object object) {
    byte[] bytes = new byte[0];
    ObjectOutputStream oo;
    ByteArrayOutputStream bo;
    try {
      //字节数组输出流在内存中创建一个字节数组缓冲区，所有发送到输出流的数据保存在该字节数组缓冲区中。
      bo = new ByteArrayOutputStream();
      oo = new ObjectOutputStream(bo);
      oo.writeObject(object);
      bytes = bo.toByteArray();
      bo.close();
      oo.close();
      return bytes;
    } catch (IOException e) {
      e.printStackTrace();
    }
      return bytes;
  }
}
