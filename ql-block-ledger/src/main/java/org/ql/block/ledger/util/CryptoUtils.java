package org.ql.block.ledger.util;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.security.ec.ECOperations;
import sun.security.ec.ECPrivateKeyImpl;
import sun.security.ec.point.AffinePoint;
import sun.security.ec.point.MutablePoint;
import sun.security.util.math.ImmutableIntegerModuloP;
import sun.security.util.math.IntegerFieldModuloP;

import java.io.*;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.Base64;
import java.util.Optional;

/**
 * Created at 2022/7/3 14:35
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Slf4j
public class CryptoUtils {


  private static final String ALGORITHM_MD5 = "MD5";
  private static final String ALGORITHM_SHA256 = "SHA-256";
  private static final String ALGORITHM_SHA1 = "SHA-1";
  private static final String CHAT_SET_UTF8 = "UTF-8";
  private static final String ENCODE_STRING_HEX = "hex";
  private static final String ENCODE_STRING_BASE64 = "base64";
  private static final String ENCODE_STRING_BASE58 = "base58";

  public static MessageDigest ripeMD160;
  public static MessageDigest sha256;

  static {
    Security.addProvider(new BouncyCastleProvider());
    try {
      ripeMD160 = MessageDigest.getInstance("RipeMD160");
      sha256 = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }

  //32个字符
  public static String encrypt_md5_hex(String str) {
    return encrypt_hash_function(str, ALGORITHM_MD5, CHAT_SET_UTF8, ENCODE_STRING_HEX);
  }

  //24个字符
  public static String encrypt_md5_base64(String str) {
    return encrypt_hash_function(str, ALGORITHM_MD5, CHAT_SET_UTF8, ENCODE_STRING_BASE64);
  }

  //22个字符
  public static String encrypt_md5_base58(String str) {
    return encrypt_hash_function(str, ALGORITHM_MD5, CHAT_SET_UTF8, ENCODE_STRING_BASE58);
  }

  //40个字符
  public static String encrypt_sha1_hex(String str) {
    return encrypt_hash_function(str, ALGORITHM_SHA1, CHAT_SET_UTF8, ENCODE_STRING_HEX);
  }


  //28个字符
  public static String encrypt_sha1_base64(String str) {
    return encrypt_hash_function(str, ALGORITHM_SHA1, CHAT_SET_UTF8, ENCODE_STRING_BASE64);
  }

  //28个字符
  public static String encrypt_sha1_base58(String str) {
    return encrypt_hash_function(str, ALGORITHM_SHA1, CHAT_SET_UTF8, ENCODE_STRING_BASE58);
  }


  //64个字符
  public static String encrypt_sha256_hex(String str) {
    return encrypt_hash_function(str, ALGORITHM_SHA256, CHAT_SET_UTF8, ENCODE_STRING_HEX);
  }

  //44个字符
  public static String encrypt_sha256_base64(String str) {
    return encrypt_hash_function(str, ALGORITHM_SHA256, CHAT_SET_UTF8, ENCODE_STRING_BASE64);
  }

  //44个字符
  public static String encrypt_sha256_base58(String str) {
    return encrypt_hash_function(str, ALGORITHM_SHA256, CHAT_SET_UTF8, ENCODE_STRING_BASE58);
  }


  private static String encrypt_hash_function(String str, String algorithm, String chatset, String encodeMethod) {
    MessageDigest messageDigest;
    String encodeStr = "";
    try {
      messageDigest = MessageDigest.getInstance(algorithm);
      messageDigest.update(str.getBytes(chatset));

      byte[] digest_bytes = messageDigest.digest();

      if (ENCODE_STRING_BASE64.equals(encodeMethod)) {
        encodeStr = byte2Base64(digest_bytes);
      } else if (ENCODE_STRING_BASE58.equals(encodeMethod)) {
        encodeStr = Base58.encode(digest_bytes);
      } else {
        encodeStr = byte2Hex(digest_bytes);
      }

    } catch (Exception e) {
      log.error("", e);
    }
    return encodeStr;
  }


  public static String byte2Base64(byte[] bytes) {
    return Base64.getEncoder().encodeToString(bytes);
  }



  public static String byte2Hex(byte[] bytes) {
    StringBuffer stringBuffer = new StringBuffer();
    String temp;
    for (int i = 0; i < bytes.length; i++) {
      //& 0xFF 的目的是去bytes[i]的后8位，
      temp = Integer.toHexString(bytes[i] & 0xFF );
      if (temp.length() == 1) {
        stringBuffer.append("0");
      }
      stringBuffer.append(temp);
    }
    return stringBuffer.toString();
  }

  /**
   * 将私钥转为可视化的数据
   * @param ecPrivateKey
   * @return
   */
  public static byte[] privateKeyFormat(ECPrivateKey ecPrivateKey){
    byte[] encoded = ecPrivateKey.getEncoded();
    PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(encoded);
    return Base64.getEncoder().encode(pkcs8EncodedKeySpec.getEncoded());
  }

  public static void privateKeyStore(File file, ECPrivateKey ecPrivateKey){
    byte[] keyFormat = privateKeyFormat(ecPrivateKey);
    try {
      FileOutputStream out = new FileOutputStream(file);
      out.write(keyFormat);
      out.close();
    } catch (FileNotFoundException e) {
      file.mkdirs();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static PrivateKey privateKeyReStore(byte[] encode){
    PrivateKey privateKey = null;
    try {
      KeyFactory ec = KeyFactory.getInstance("EC");
      privateKey = ec.generatePrivate(new PKCS8EncodedKeySpec(encode));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (InvalidKeySpecException e) {
      e.printStackTrace();
    }
    return privateKey;
  }

  /**
   * 参考源码ECKeyPairGenerator.generateKeyPairImpl();
   * 通过私钥生产公钥
   * @param privateKey
   * @return
   */
  public static ECPublicKey priGenPub(ECPrivateKeyImpl privateKey){
    KeyFactory ec = null;
    try {
      ec = KeyFactory.getInstance("EC");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    ECPoint G = privateKey.getParams().getGenerator();
    ECParameterSpec params = privateKey.getParams();
    //var1: 通过私钥中的params转换为的一个EC操作对象
    Optional var1 = ECOperations.forParameters(params);
    ECOperations var2 = (ECOperations) var1.get();
    //var3: 椭圆曲线中的k，即私钥的“值”
    byte[] var3 = privateKey.getArrayS();
    //var4: 一个EC操作的字段。
    IntegerFieldModuloP var4 = var2.getField();

    //var5 - var7 为了将生成点G转换为AffinePoint
    ImmutableIntegerModuloP var5 = var4.getElement(G.getAffineX());
    ImmutableIntegerModuloP var6 = var4.getElement(G.getAffineY());
    AffinePoint var7 = new AffinePoint(var5, var6);

    //将G与k相乘得到公钥的位置
    MutablePoint var8 = var2.multiply(var7, var3);
    AffinePoint var9 = var8.asAffine();
    ECPoint var10 = new ECPoint(var9.getX().asBigInteger(), var9.getY().asBigInteger());
    //通过公钥位置生产公钥;
    ECPublicKey publicKey = null;
    try {
      publicKey = (ECPublicKey) ec.generatePublic(new ECPublicKeySpec(var10,params));
    } catch (InvalidKeySpecException e) {
      e.printStackTrace();
    }
    return publicKey;
  }
}
