package org.ql.block.ledger.model.blockdata;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TXOutput)) return false;

    TXOutput txOutput = (TXOutput) o;

    if (value != txOutput.value) return false;
    return Objects.equals(scriptPubKey, txOutput.scriptPubKey);
  }

  @Override
  public int hashCode() {
    int result = value;
    result = 31 * result + (scriptPubKey != null ? scriptPubKey.hashCode() : 0);
    return result;
  }
}
