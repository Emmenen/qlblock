package org.ql.block.ledger.wallet;

import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

/**
 * Created at 2022/7/16 3:07
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Component
public class Identify{
  private ECPrivateKey privateKey;
  private ECPublicKey publicKey;

  public Identify() {
  }

  public Identify(KeyPair keyPair) {
    this.privateKey = (ECPrivateKey) keyPair.getPrivate();
    this.publicKey = (ECPublicKey) keyPair.getPublic();
  }

  public Identify(ECPrivateKey privateKey, ECPublicKey publicKey) {
    this.privateKey = privateKey;
    this.publicKey = publicKey;
  }

  public ECPrivateKey getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(ECPrivateKey privateKey) {
    this.privateKey = privateKey;
  }

  public ECPublicKey getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(ECPublicKey publicKey) {
    this.publicKey = publicKey;
  }
}
