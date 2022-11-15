package org.ql.block.peer.communication.message.peer.pojo;

import org.ql.block.ledger.model.blockdata.Transaction;
import org.springframework.beans.BeanUtils;

/**
 * Created at 2022/10/12 20:44
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class TransactionMessage extends Transaction implements PeerMessage {

  public TransactionMessage(Transaction transaction) {
    super(transaction.reward);
    BeanUtils.copyProperties(transaction,this);
  }

  @Override
  public long UID() {
    return hashCode()+System.currentTimeMillis();
  }
}
