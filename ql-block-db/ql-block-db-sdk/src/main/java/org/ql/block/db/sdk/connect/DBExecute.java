package org.ql.block.db.sdk.connect;


import org.ql.block.db.share.message.ResponseVo;

import java.util.Map;


/**
 * Created at 2022/12/18 14:52
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class DBExecute {

  private Connection connection;


  /**
   * create bucketName
   * drop bucketName
   * insert bucketName key value
   * delete bucketName key
   * update bucketName key value
   * select bucketName key
   */
  private String option;

  public DBExecute(Connection connection, String option) {
    this.connection = connection;
    this.option = option;
  }

  public ResponseVo execute(){
    ResponseVo responseVo = connection.sendCommand(option);
    return responseVo;
  }
  public ResponseVo<Map.Entry<byte[],byte[]>> selectBatch(){
    ResponseVo<Map.Entry<byte[],byte[]>> responseVo = connection.sendCommand(option);
    return responseVo;
  }

}
