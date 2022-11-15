package org.ql.block.ledger.model.utxo;

import org.ql.block.ledger.model.blockdata.TXOutput;

/**
 * Created at 2022/10/27 9:45
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class UnSpentOutput extends TXOutput {
  public String txHash;
  public String txId;
  public Integer txOutputN;
  public Long confirmations;

  public UnSpentOutput(TXOutput txOutput,Integer txOutputN,String txId) {
    this.txOutputN = txOutputN;
    this.txId = txId;
  }
}
