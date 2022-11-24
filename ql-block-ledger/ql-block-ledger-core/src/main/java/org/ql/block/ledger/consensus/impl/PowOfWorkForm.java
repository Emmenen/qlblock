package org.ql.block.ledger.consensus.impl;

import org.ql.block.ledger.consensus.ConsensusVO;

import java.math.BigInteger;

/**
 * Created at 2022/7/3 17:32
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class PowOfWorkForm extends ConsensusVO {
  public String hash;
  public BigInteger nonce;
  public String miner;
}
