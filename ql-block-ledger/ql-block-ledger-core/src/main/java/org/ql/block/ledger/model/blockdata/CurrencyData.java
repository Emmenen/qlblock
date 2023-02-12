package org.ql.block.ledger.model.blockdata;

import java.io.Serializable;

/**
 * Created at 2022/9/29 17:04
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class CurrencyData extends BlockData implements Serializable {

  public CurrencyData(Transaction[] transactions) {
    super(transactions);
  }

}
