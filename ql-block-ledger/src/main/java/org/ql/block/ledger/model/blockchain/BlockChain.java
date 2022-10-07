package org.ql.block.ledger.model.blockchain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.jetbrains.annotations.NotNull;
import org.ql.block.ledger.db.Database;
import org.ql.block.ledger.exceptions.AddBlockError;
import org.ql.block.ledger.exceptions.BlockOrderError;
import org.ql.block.ledger.exceptions.DataBaseIsNotExistError;
import org.ql.block.ledger.exceptions.GetBlockError;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.model.block.MasterBlock;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.util.MathUtils;
import org.ql.block.ledger.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.ql.block.ledger.util.ObjectUtil.ObjectToByteArray;
import static org.ql.block.ledger.util.ObjectUtil.byteArrayToObject;

/**
 * Created at 2022/6/29 20:09
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Slf4j
@EnableTransactionManagement
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

    this.bucketBucketDb = this.database.getBucket(BLOCK_BUCKET);
    if (null==bucketBucketDb) {
      this.bucketBucketDb = database.createBucket(BLOCK_BUCKET);
      Block block = this.newGenesisBlock();
      this.deep = -1;
      addBlock(block);
    }else {
      this.tip = new String(bucketBucketDb.get(Iq80DBFactory.bytes("l")));
      log.info("最新区块Hash："+this.tip);
      this.deep = this.getHeight();
      byte[] d = bucketBucketDb.get(Iq80DBFactory.bytes("d"));
      this.deep = MathUtils.byteArrayToInt(d);
      log.info("区块高度："+this.deep);
    }
  }

  public abstract Block newGenesisBlock();


  public void addBlock(BlockData data){
    MasterBlock block;
    block = new MasterBlock(tip, data);
    addBlock(block);
  }

  /**
   * 区块的高度的下标是从1开始的;
   * @param offset 从offset高度开始取
   * @param number 取offset之后的number个
   * @return
   * @throws GetBlockError
   */
  public LinkedList<Block> getBlocks(int offset, int number) throws GetBlockError {
    if (offset<1){
      log.error("取出区块异常, offset 的值最小为1");
      throw new GetBlockError("取出区块异常, offset 的值最小为1");
    }
    if (number<1){
      log.error("取出区块异常, number 的值最小为1");
      throw new GetBlockError("取出区块异常, number 的值最小为1");
    }
    Integer height = getHeight();
    LinkedList<Block> blockList = new LinkedList<>();
    int var1 = height - offset;
    if (var1<0){
      log.error("取出区块异常, offset 大于当前区块高度");
      throw new GetBlockError("取出区块异常, offset 大于当前区块高度");
    }else {
      //从账本顶部开始检索，找到offset处，从offset往后取出number个
      int count = 0;
      Block block = getBlock(tip);
      blockList.addLast(block);
      count++;
      int var2 = var1 + 1;
      while (count<var2){
        block = getBlock(block.previousHash);
        blockList.addLast(block);
        count++;
      }
    }

    for (int i = 0; i < blockList.size() - number; i++) {
      blockList.removeLast();
    }
    return blockList;
  }

  public Integer getHeight(){
    return MathUtils.byteArrayToInt(getData("d"));
  }

  public String getLastHash(){
    return new String(getData("l"));
  }

  public void addBlock(@NotNull Block block) {
    this.tip = block.currentHash;
    this.deep++;
    log.info("当前区块高度："+deep);
    database.update(BLOCK_BUCKET, bucket ->{
      bucket.put(Iq80DBFactory.bytes(block.currentHash),ObjectToByteArray(block));
      log.info("添加新的区块到链上(perHash: {})",block.previousHash);
      log.info("添加新的区块到链上(currentHash: {})",block.currentHash);
      bucket.put(Iq80DBFactory.bytes("l"),Iq80DBFactory.bytes(tip));
      bucket.put(Iq80DBFactory.bytes("d"),MathUtils.intToByteArray(deep));
    });
    //放后面的作用是先存放区块高度再+1，因为创始区块的高度为0;
    log.info("当前区块高度："+getHeight());
  }

  public boolean addBlockAllStrict(Collection<Block> c)  {
    // 保证最后添加到账本中的区块是最新的就可以；
    Iterator<Block> iterator = c.iterator();
    Block first = iterator.next();
    if (getLastHash().equals(first.getPreviousHash())) {
      throw new BlockOrderError("区块清单顺序有误！");
    }
    String preHash = first.getCurrentHash();
    addBlock(first);
    String previousHash;
    String currentHash;

    while (iterator.hasNext()){

      Block next = iterator.next();
      previousHash = next.getCurrentHash();

      if (!Objects.equals(previousHash, preHash)){
        throw new BlockOrderError("区块清单顺序有误！");
      }
      addBlock(next);
      currentHash = next.getCurrentHash();
      preHash = currentHash;
    }
    return true;
  }

  public byte[] getData(String key){
    byte[] bytes = bucketBucketDb.get(Iq80DBFactory.bytes(key));
    return bytes;
  }

  public Block getBlock(String blockHash){
    byte[] data = getData(blockHash);
    return (Block) byteArrayToObject(data);
  }

  public DB getBlockDB(){
    return this.bucketBucketDb;
  }

}
