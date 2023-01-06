package org.ql.block.ledger.wallet;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.ql.block.common.exceptions.WalletInformationError;
import org.ql.block.ledger.communication.wallet.WalletMessageVo;
import org.ql.block.ledger.communication.wallet.enums.WalletType;
import org.ql.block.ledger.communication.wallet.message.BeConnected;
import org.ql.block.ledger.util.ArrayUtils;
import org.ql.block.ledger.util.Base58;
import org.ql.block.ledger.util.CryptoUtils;
import sun.security.ec.ECPrivateKeyImpl;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created at 2022/7/3 13:52
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Slf4j
public class Wallet {
  private static ECPoint G = new ECPoint(new BigInteger("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296",16),new BigInteger("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5",16));

  private BlockingQueue<WalletMessageVo> walletBlockingQueue = new LinkedBlockingQueue<>();

  private Identify user;

  private String path;

  private boolean fromFile;

  @Override
  public String toString() {
    return "Wallet{"
             + getAddress() +
            '}';
  }

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  public static final String version = String.valueOf(0x00);

  public Wallet(String fistPath,String... child){
    Path path = Paths.get(fistPath, child);
    File file = path.toFile();
    this.path = file.getPath();
    file.mkdir();
    fromFile = true;
  }

  public Identify getUser() {
    return user;
  }

  public Wallet() {
    fromFile = false;
  }

  public boolean isConnected(){
    boolean flag = false;
    if (getUser()!=null){
      flag = getUser().isConnected();
    }
    return flag;
  }

  public boolean waitConnected() throws InterruptedException {
    WalletMessageVo take = walletBlockingQueue.take();
    while (!take.getWalletType().equals(WalletType.WALLET_BE_CONNECTED)){
      walletBlockingQueue.put(take);
    }
    if (isConnected())
    return true;
    else {
      log.warn("钱包有被攻击的风险！");
      return false;
    }
  }

  public void setUser(Identify user) throws WalletInformationError {
    try {
      walletBlockingQueue.put(new WalletMessageVo(WalletType.WALLET_BE_CONNECTED, new BeConnected()));
      this.user = user;
    } catch (InterruptedException e) {
      throw new WalletInformationError("钱包绑定出错！");
    }
  }

  public Identify connectUser(String priKey) throws WalletInformationError {
    byte[] privateEncode = Base58.decodeToByte(priKey);
    ECPrivateKeyImpl privateKey = (ECPrivateKeyImpl) CryptoUtils.privateKeyReStore(privateEncode);
    Identify user = new Identify(privateKey,CryptoUtils.priGenPub(privateKey));;
    setUser(user);
    return user;
  }


  @Deprecated
  public Identify connectUserFromFile(String name) throws WalletInformationError {
    if (!fromFile){
      log.error("不支持的连接方式，请先实例化一个文件类型的Wallet");
      return null;
    }
    File file = new File(path, name+".id");
    byte[] bytes = new byte[1024];
    StringBuilder privateKeyBase58Buffer = new StringBuilder();
    Identify user = null;
    if (file.exists()){
      try {
        FileInputStream fin = new FileInputStream(file);
        int len = 0;
        while ( (len=fin.read(bytes))!=-1){
          privateKeyBase58Buffer.append(new String(bytes,0,len));
        }
        String privateKeyBase64 = privateKeyBase58Buffer.toString();
        user = connectUser(privateKeyBase64);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    setUser(user);
    return user;
  }

  /**
   * 生产一个新的身份
   * @param name
   * @return
   */
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
