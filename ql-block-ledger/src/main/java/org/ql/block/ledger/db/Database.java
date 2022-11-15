package org.ql.block.ledger.db;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.ql.block.ledger.exceptions.DataBaseIsNotExistError;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created at 2022/6/29 20:36
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public  class Database implements Serializable {
  //用来存放数据库中有哪些bucket的数据库的名称;
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

  public Database(String name,DB db){
    this.name = name;
    homeDB = db;
    this.init();
  }

  public Database(String name) {
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

  protected void init(){
    byte[] bytes = homeDB.get(Iq80DBFactory.bytes(DB_INIT));
    if (null == bytes||bytes.length==0){
      //将key(DB_INIT)赋值,说明该数据库是已经初始化的数据库
      homeDB.put(Iq80DBFactory.bytes(DB_INIT),Iq80DBFactory.bytes(DB_INIT));
    }
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
  public void update(String buketName,Consumer< DB > consumer){
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
  public Database connect(String dbName) throws DataBaseIsNotExistError {
    DB db = getBucket(dbName);
    Database database = null;
    if (null==db){
      throw new DataBaseIsNotExistError("连接数据库失败，数据库不存在！");
    }else {
      database = new Database(name+"/"+dbName, db);
    }
    return database;
  }

  public Database createDatabase(String dbName){
    DB db = getBucket(dbName);
    Database database;
    if (null==db){
      db = this.createBucket(dbName);
    }
    database = new Database(name+"/"+dbName, db);
    return database;
  }
}
