package org.ql.block.db.service.impl;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.ql.block.db.service.DataBase;
import org.ql.block.db.service.exceptions.DataBaseIsNotExistError;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Created at 2022/6/29 20:36
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class DataBaseImpl<T> implements DataBase,Serializable {
  //判断数据库是否初始化;
  public static final String DB_INIT = "Initialized";

  private String name;

  private static DB homeDB;
  private HashMap<String,DB> bucketMap = new HashMap<>();

  private static final DBFactory dbFactory;
  private static  final Options options;

  static {
    dbFactory = new Iq80DBFactory();
    options = new Options();
    options.createIfMissing(true);
  }

  public DataBaseImpl(String name, DB db){
    this.name = name;
    homeDB = db;
    this.init();
  }

  public DataBaseImpl(String name) {
    this.name = name;
    try {
      homeDB = dbFactory.open(new File(this.name), options);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Error error){
      error.printStackTrace();
    }
    this.init();
  }

  public void init(){
    byte[] bytes = homeDB.get(Iq80DBFactory.bytes(DB_INIT));
    if (null == bytes||bytes.length==0){
      //将key(DB_INIT)赋值,说明该数据库是已经初始化的数据库
      homeDB.put(Iq80DBFactory.bytes(DB_INIT),Iq80DBFactory.bytes(DB_INIT));
    }
  }

  @Override
  public Class getBucketClass() {
    return DB.class;
  }

  public DB getBucket(String bucketName){
    //获取homeDB中存放的bucketList
    byte[] bytes = homeDB.get(Iq80DBFactory.bytes(bucketName));
    //如果exist为null或为false说明区块中没有当前bucket;
    if (null!=bytes && bytes.length!=0){
      DB bucket =  this.bucketMap.get(bucketName);
      if (bucket == null ){
        if (bucketName != DB_INIT){
          try {
            bucket = dbFactory.open(new File(name, bucketName), options);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }else {
          //如果取的bucket是bucketName则直接返回homeDB;
          bucket = homeDB;
        }
        this.bucketMap.put(bucketName,bucket);
      }
      return bucket;
    }else {
      return null;
    }
  }
  public void deleteBuket(String bucketName){
    this.bucketMap.remove(bucketName);
    File file = new File(name, bucketName);
    if (file.exists()){
      file.deleteOnExit();
    }
  }
  public DB createBucket(String bucketName){
    File file = new File(name, bucketName);
    DB db = null;
    try {
      db = dbFactory.open(file, options);
      bucketMap.put(bucketName, db);
      homeDB.put(Iq80DBFactory.bytes(bucketName),Iq80DBFactory.bytes(bucketName));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return db;
  }

  @Override
  public DB createIfNotExistBuket(String bucketName) {
    DB bucket = getBucket(bucketName);
    if (bucket==null){
      bucket =  createBucket(bucketName);
    }
    return bucket;
  }


  public void update(String buketName,Consumer consumer){
    consumer.accept(getBucket(buketName));
  }
  public void close() throws IOException {
    bucketMap.forEach((s, entries) ->
    {
      try {
        entries.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * 通过文件（数据库名）连接到一个数据库
   * @param dbName
   * @return
   */
  public DataBaseImpl connect(String dbName) throws DataBaseIsNotExistError {
    DB db = getBucket(dbName);
    DataBaseImpl databaseImpl = null;
    if (null==db){
      throw new DataBaseIsNotExistError("连接数据库失败，数据库不存在！");
    }else {
      databaseImpl = new DataBaseImpl(name+"/"+dbName, db);
    }
    return databaseImpl;
  }

  public DataBaseImpl createDatabase(String dbName){
    DB db = getBucket(dbName);
    DataBaseImpl databaseImpl;
    if (null==db){
      db = this.createBucket(dbName);
    }
    databaseImpl = new DataBaseImpl(name+"/"+dbName, db);
    return databaseImpl;
  }
}
