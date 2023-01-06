package org.ql.block.ledger.consensus;

import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.wallet.Wallet;
import org.springframework.stereotype.Service;

/**
 * Created at 2022/11/22 10:14
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Service
public interface Consensus<T extends ConsensusVO> {
  public Wallet getWallet();
  public boolean validate(Block block);
  public T run(Block block);
}
