package org.ql.block.ledger.model.blockdata;

import java.io.Serializable;

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

}
