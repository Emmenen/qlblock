package org.ql.block.ledger.model.blockchain;


import org.ql.block.ledger.config.LedgerConfig;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.service.DatabaseService;

import static org.ql.block.ledger.util.ObjectUtil.byteArrayToObject;

/**
 * Created at 2022/10/26 10:13
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class BLockChainIterator {
  public String currentHash;
  public DatabaseService databaseService;

  public BLockChainIterator(String currentHash, DatabaseService databaseService) {
    this.currentHash = currentHash;
    this.databaseService = databaseService;
  }
  public Block next(){
    String one = (String) databaseService.selectOne(LedgerConfig.BLOCK_BUCKET, currentHash);
    Block block =  Block.formByte(one.getBytes());
    currentHash = block.previousHash;
    if (block.getPreviousHash().equals("genesis")){
      return null;
    }
    return block;
  }
  public boolean hashNext(){
    if (currentHash.equals("genesis")){
      return false;
    }else
      return true;
  }
}
