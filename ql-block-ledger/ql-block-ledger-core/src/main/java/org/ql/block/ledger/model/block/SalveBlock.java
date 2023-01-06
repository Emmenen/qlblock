package org.ql.block.ledger.model.block;


import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.wallet.Wallet;

import java.security.PublicKey;
import java.util.Arrays;

/**
 * Created at 2022/6/29 20:01
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class SalveBlock extends Block {
  public PublicKey publicKey;
  public PublicKey[] managerGroup;
  
  public SalveBlock(String previousHash, BlockData data) {
    super(previousHash, data);
  }

  public SalveBlock(PublicKey publicKey, PublicKey[] managerGroup) {
    this.publicKey = publicKey;
    this.managerGroup = managerGroup;
  }
  public SalveBlock(PublicKey publicKey, PublicKey managerPublicKey) {
    this.publicKey = publicKey;
    if (this.managerGroup==null){
      this.managerGroup = new PublicKey[1];
      this.managerGroup[0] = managerPublicKey;
    }else {
      int length = this.managerGroup.length;
      PublicKey[] publicKeys = Arrays.copyOf(this.managerGroup, length + 1);
      publicKeys[length] = managerPublicKey;
      this.managerGroup = publicKeys;
    }
  }

  public static SalveBlock newGenesisSalve(PublicKey publicKey, PublicKey managerPublicKey){
    return new SalveBlock(publicKey, managerPublicKey);
  }

  @Override
  public void setHash() {
    super.setHash();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SalveBlock)) return false;

    SalveBlock that = (SalveBlock) o;

    if (publicKey != null ? !publicKey.equals(that.publicKey) : that.publicKey != null) return false;
    // Probably incorrect - comparing Object[] arrays with Arrays.equals
    return Arrays.equals(managerGroup, that.managerGroup);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (publicKey != null ? new String(publicKey.getEncoded()).hashCode() : 0);
    result = 31 * result + Arrays.hashCode(managerGroup);
    return result;
  }
}
