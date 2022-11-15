package org.ql.block.ledger.model.blockchain;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.ql.block.ledger.model.block.Block;

import static org.ql.block.ledger.util.ObjectUtil.byteArrayToObject;

/**
 * Created at 2022/10/26 10:13
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class BLockChainIterator {
  public String currentHash;
  public DB db;

  public BLockChainIterator(String currentHash, DB db) {
    this.currentHash = currentHash;
    this.db = db;
  }
  public Block next(){
    Block block = (Block)byteArrayToObject(db.get(db.get(Iq80DBFactory.bytes(currentHash))));
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
