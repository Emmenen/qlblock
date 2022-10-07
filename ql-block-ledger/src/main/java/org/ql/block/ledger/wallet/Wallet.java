package org.ql.block.ledger.wallet;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.ql.block.ledger.util.ArrayUtils;
import org.ql.block.ledger.util.Base58;
import org.ql.block.ledger.util.CryptoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import sun.security.ec.ECPrivateKeyImpl;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.util.Base64;

/**
 * Created at 2022/7/3 13:52
 * Author: @Qi Long
 * email: 592918942@qq.com
 */

public class Wallet {
  private static ECPoint G = new ECPoint(new BigInteger("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296",16),new BigInteger("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5",16));

  private Identify user;

  private String path;

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  public static final String version = String.valueOf(0x00);

  public Wallet(String fistPath,String... child){
    Path path = Paths.get(fistPath, child);
    File file = path.toFile();
    this.path = file.getPath();
    file.mkdir();
  }


  public Identify user(String name){
    File file = new File(path, name+".id");
    byte[] bytes = new byte[1024];
    StringBuffer privateKeyBase64Buffer = new StringBuffer();
    Identify user = null;
    if (file.exists()){
      try {
        FileInputStream fin = new FileInputStream(file);
        int len = 0;
        while ( (len=fin.read(bytes))!=-1){
          privateKeyBase64Buffer.append(new String(bytes,0,len));
        }
        String privateKeyBase64 = privateKeyBase64Buffer.toString();
        byte[] privateEncode = Base64.getDecoder().decode(privateKeyBase64.getBytes());

        ECPrivateKeyImpl privateKey = (ECPrivateKeyImpl) CryptoUtils.privateKeyReStore(privateEncode);

        user = new Identify(privateKey,CryptoUtils.priGenPub(privateKey));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    this.user = user;
    return user;
  }

  public Identify createIdentify(String name) {
    Identify identify = null;
    try {
      KeyPairGenerator ec = KeyPairGenerator.getInstance("EC");
      KeyPair keyPair = ec.generateKeyPair();
      identify = new Identify();
      identify.setPrivateKey((ECPrivateKey) keyPair.getPrivate());
      identify.setPublicKey((ECPublicKey) keyPair.getPublic());

      /**
       * 将新建的Wallet持久化到数据库或文件；
       */
      CryptoUtils.privateKeyStore(new File(path,name+".id"),identify.getPrivateKey());
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return identify;
  }

  public String getAddress(){
    //计算公钥hash
    byte[] publicKeyHash = hashPublicKey();
    byte[] versionBytes = version.getBytes();
    //附加版本信息
    byte[] versionedPayload = ArrayUtils.byteArraySum(versionBytes,publicKeyHash);
    byte[] checkSum = checkSum(versionedPayload);
    byte[] address = ArrayUtils.byteArraySum(versionedPayload, checkSum);
//    return Base64.toBase64String(address);
    return Base58.encode(address);
  }

  public byte[] hashPublicKey(){
    byte[] address = CryptoUtils.ripeMD160.digest(CryptoUtils.sha256.digest(user.getPublicKey().getEncoded()));
    return address;
  }

  /**
   * 计算校验和
   * @return
   */
  public byte[] checkSum(byte[] payLoad){
    byte[] bytes = new byte[4];
    byte[] hash1 = CryptoUtils.sha256.digest(payLoad);
    byte[] hash2 = CryptoUtils.sha256.digest(hash1);
    System.arraycopy(hash2,0,bytes,0,4);
    return bytes;
  }


}
