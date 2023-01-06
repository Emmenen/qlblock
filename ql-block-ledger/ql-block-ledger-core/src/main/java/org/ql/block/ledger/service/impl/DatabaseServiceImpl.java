package org.ql.block.ledger.service.impl;

import org.ql.block.db.sdk.connect.Connection;
import org.ql.block.db.share.enums.CommandEnum;
import org.ql.block.db.share.message.Operation;
import org.ql.block.db.share.message.ResponseVo;
import org.ql.block.db.share.utils.Iq80Factory;
import org.ql.block.ledger.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Created at 2022/12/24 15:43
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Service("databaseService")
public class DatabaseServiceImpl implements DatabaseService {

  @Autowired
  private Connection connection;

  @Override
  public void useDatabase(String databaseName) {
    connection.preExecute("use "+databaseName).execute();
  }

  @Override
  public void createDatabase(String databaseName) {
    connection.preExecute("create database "+databaseName).execute();
  }

  @Override
  public ResponseVo createBucket(String bucketName) {
    return connection.preExecute("create bucket " + bucketName).execute();
  }

  @Override
  public ResponseVo deleteBucket(String bucketName) {
    return connection.preExecute("drop bucket " + bucketName).execute();

  }


  @Override
  public void insertOrUpdate(String bucket, String key, byte[] value) {
    insertOrUpdate(bucket,Iq80Factory.bytes(key),value);

  }

  @Override
  public void insertOrUpdate(String bucket, String key, byte value) {
    insertOrUpdate(bucket,Iq80Factory.bytes(key),new byte[]{value});

  }

  @Override
  public void insertOrUpdate(String bucket, String key, int value) {

    insertOrUpdate(bucket,Iq80Factory.bytes(key),new byte[]{(byte) value});

  }

  @Override
  public void insertOrUpdate(String bucket, byte[] key, byte[] value) {
    Operation operation = new Operation();
    operation.setCommand(CommandEnum.INSERT);
    operation.setTarget(bucket);
    operation.setKey(key);
    operation.setValue(value);
    connection.execute(operation);
  }

  @Override
  public void insertOrUpdate(String bucket, byte key, byte value) {
    insertOrUpdate(bucket,new byte[]{key},new byte[]{value});
  }

  @Override
  public void insertOrUpdate(String bucket, int key, int value) {
    insertOrUpdate(bucket,new byte[]{(byte) key},new byte[]{(byte) value});

  }


  @Override
  public void delete(String bucket, String key) {
    connection.preExecute("delete "+ bucket +" "+key).execute();
  }

  @Override
  public ResponseVo select(String bucket, String key) {
    ResponseVo responseVo = connection.preExecute("select " + bucket + " " + key).execute();
    return responseVo;
  }

  @Override
  public Object selectOne(String bucket, String key) {
    ResponseVo select = select(bucket, key);
    Object next = select.getData().iterator().next();
    return next;
  }

  @Override
  public Integer getCount(String bucket) {
    ResponseVo responseVo = connection.preExecute("count" + bucket).execute();
    Collection data = responseVo.getData();
    return null;
  }

  @Override
  public ResponseVo<Map.Entry<byte[], byte[]>> select(String bucket, int offset, int size) {
    ResponseVo<Map.Entry<byte[], byte[]>> mapResponseVo = connection.preExecute("select " + bucket + " * " + " limit " + offset + " " + size).selectBatch();
    return mapResponseVo;
  }
}
