package org.ql.block.ledger.model.blockdata;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created at 2022/7/13 23:49
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class TXInput implements Serializable {
  private static final long serialVersionUID = 111;

  public String Txid;
  //在Transaction中的所有TxOutput中该TxInput使用的是第几条的索引
  public int vOutIndex;
  //用于解锁上面交易输出的脚本;
//  Consumer script;
  public String script;

  public boolean CanUnlockOutputWith(String address){
    if (script.equals(address)){
      return true;
    }else {
      return false;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TXInput)) return false;

    TXInput txInput = (TXInput) o;

    if (vOutIndex != txInput.vOutIndex) return false;
    if (!Objects.equals(Txid, txInput.Txid)) return false;
    return Objects.equals(script, txInput.script);
  }

  @Override
  public int hashCode() {
    int result = Txid != null ? Txid.hashCode() : 0;
    result = 31 * result + vOutIndex;
    result = 31 * result + (script != null ? script.hashCode() : 0);
    return result;
  }
}
