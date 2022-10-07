package org.ql.block.ledger.model.blockchain;

import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.jetbrains.annotations.NotNull;
import org.ql.block.ledger.db.Database;
import org.ql.block.ledger.exceptions.DataBaseIsNotExistError;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.ql.block.ledger.util.ObjectUtil.ObjectToByteArray;

/**
 * Created at 2022/6/29 20:09
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Repository
@Slf4j
public abstract class BlockChain {


  protected static final String BLOCK_BUCKET = "blockBucket";
  protected DB bucketBucketDb;

  /**
   * 存放区块链数据的bucket名称
   */
  public String ChainName;
  /**
   * 区块链的最上层区块的hash
   */
  public String tip;
  /**
   * 区块深度
   */
  public int deep;

  public Database database;


  @Autowired
  public BlockChain(Database staticDatabase) {
    this.database = staticDatabase;
  }

  protected void init(Database staticDatabase){
    try {
      this.database = staticDatabase.connect(ChainName);
    } catch (DataBaseIsNotExistError e) {
      staticDatabase.createBucket(ChainName);
      this.database = staticDatabase.createDatabase(ChainName);
    }

    bucketBucketDb = this.database.getBucket(BLOCK_BUCKET);
    if (null==bucketBucketDb) {
      database.createBucket(BLOCK_BUCKET);
      Block block = this.newGenesisBlock();
      addBlock(block);
    }else {
      this.tip = new String(bucketBucketDb.get(Iq80DBFactory.bytes("l")));
      log.info("最新区块Hash："+this.tip);
      this.deep = MathUtils.byteArrayToInt(bucketBucketDb.get(Iq80DBFactory.bytes("d")));
      log.info("区块高度："+this.deep);

    }
  }

  public abstract Block newGenesisBlock();

  public abstract void addBlock(BlockData data);

  public void addBlock(@NotNull Block block) {
    this.tip = block.currentHash;
    DB databaseBucket = database.getBucket(BLOCK_BUCKET);

    this.deep = MathUtils.byteArrayToInt(databaseBucket.get(Iq80DBFactory.bytes("d")));
    log.info("当前区块高度："+deep);
    this.deep++;
    database.update(BLOCK_BUCKET, bucket ->{
      bucket.put(Iq80DBFactory.bytes(block.currentHash),ObjectToByteArray(block));
      log.info("添加新的区块到链上（hash: {}）",block.currentHash);
      bucket.put(Iq80DBFactory.bytes("l"),Iq80DBFactory.bytes(tip));
      bucket.put(Iq80DBFactory.bytes("d"),MathUtils.intToByteArray(deep));
      log.info("当前区块高度："+deep);
    });
  }

  public DB getBlock(){
    return database.getBucket(BLOCK_BUCKET);
  }

}
