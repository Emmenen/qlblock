package org.ql.block.db.config;

import org.iq80.leveldb.DB;
import org.ql.block.db.service.DataBase;
import org.ql.block.db.service.impl.DataBaseImpl;

/**
 * Created at 2022/12/19 19:58
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class DatabaseConfig {

  public static final String dbName = "levelDb";
  public static DataBase<DB> dataBase;

  public static DataBase levelDB(){
    if (dataBase == null){
      dataBase = new DataBaseImpl(dbName);
    }
    return dataBase;
  }

}
