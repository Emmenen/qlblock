package org.ql.block.db.service;



import org.ql.block.db.share.exceptions.DataBaseIsNotExistError;
import org.ql.block.db.share.exceptions.OptionalSyntaxException;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Created at 2022/11/24 10:50
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public interface DataBase<T> {

  void init();

  public Class getBucketClass();

  public void deleteBuket(String bucketName);

  public boolean createBucket(String bucketName) throws OptionalSyntaxException;

  public T getBucket(String bucketName);

  public void update(String buketName, Consumer<T> consumer);

  public DataBase connect(String dbName) throws DataBaseIsNotExistError;

  public DataBase createDatabase(String dbName);

  public DataBase getDatabase(String dbName);

  public void deleteDatabase(String dbName);

  public void close() throws IOException;

}
