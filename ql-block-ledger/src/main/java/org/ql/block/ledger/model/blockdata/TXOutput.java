package org.ql.block.ledger.model.blockdata;

import java.io.Serializable;

/**
 * Created at 2022/7/13 23:49
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class TXOutput implements Serializable {
  private static final long serialVersionUID = 111;

  public int value;
  public String scriptPubKey;

  public TXOutput() {
  }

  public TXOutput(int value, String scriptPubKey) {
    this.value = value;
    this.scriptPubKey = scriptPubKey;
  }

  public boolean CanBeUnlockedWith(String address){
    if (scriptPubKey.equals(address)){
      return true;
    }else {
      return false;
    }
  }

}
