package org.ql.block.ledger.service;

import org.ql.block.db.share.message.ResponseVo;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created at 2022/12/24 15:43
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Service
public interface DatabaseService {
  public void useDatabase(String databaseName);
  public void createDatabase(String databaseName);
  public ResponseVo createBucket(String bucketName);
  public ResponseVo deleteBucket(String bucketName);
  public void insertOrUpdate(String bucket,String key,byte[] value);
  public void insertOrUpdate(String bucket,String key,byte value);
  public void insertOrUpdate(String bucket,String key,int value);
  public void insertOrUpdate(String bucket,byte[] key,byte[] value);
  public void insertOrUpdate(String bucket,byte key,byte value);
  public void insertOrUpdate(String bucket,int key,int value);
  public void delete(String bucket,String key);
  public ResponseVo select(String bucket, String key);
  public Object selectOne(String bucket, String key);
  public Integer getCount(String bucket);
  public ResponseVo<Map.Entry<byte[],byte[]>> select(String bucket, int offset, int size);
}
