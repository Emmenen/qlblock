package org.ql.block.db.connect;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.ql.block.db.service.DataBase;
import org.ql.block.db.config.DatabaseConfig;
import org.ql.block.db.config.ThreadFactory;

import org.ql.block.db.share.exceptions.OptionalSyntaxException;
import org.ql.block.db.share.message.Limit;
import org.ql.block.db.share.message.Operation;
import org.ql.block.db.share.message.ResponseVo;
import org.ql.block.db.share.utils.OperationParse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;



/**
 * Created at 2022/12/24 10:48
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Service
@Slf4j
public class ConnectionPool {

  public void startAConnection(Socket socket){
    Callable stringCallable = () -> {
      InputStream in = socket.getInputStream();
      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      boolean first = true;
      DataBase<DB> dataBase = null;
//          if (first){
//            dataBase = DatabaseConfig.levelDB().getDatabase(sb.toString());
//            first = false;
//            continue;
//          }
      dataBase = DatabaseConfig.levelDB();
      while (true){
        try {
          byte[] bytes = new byte[1024];
          int len = -1;
          StringBuilder sb = new StringBuilder();
          while ((len = in.read(bytes)) != -1){
            sb.append(new String(bytes,0,len));
            if (in.available()==0)break;
          }
          Operation operation = OperationParse.toOperation(sb.toString());
          String bucketName = operation.getTarget();
          log.info(operation.toString());

          ResponseVo res = null;
          switch (operation.getCommand()){
            case USE:
              dataBase = dataBase.getDatabase(operation.getDatabase());
              break;
            case CREATE:
              switch (operation.getType()){
                case DATABASE:
                  dataBase.createDatabase(operation.getTarget());
                  res = ResponseVo.okJson(operation.getUid(),null);
                  break;
                case BUCKET:
                  if (dataBase.createBucket(operation.getTarget())) {
                    res = ResponseVo.okJson(operation.getUid(),null);
                  }else {
                    res = ResponseVo.status(30001,null);
                  }
                  break;
              }
              break;
            case DROP:
              switch (operation.getType()){
                case DATABASE:
                  dataBase.deleteDatabase(operation.getTarget());
                  res = ResponseVo.okJson(operation.getUid(),null);
                  break;
                case BUCKET:
                  dataBase.deleteBuket(operation.getTarget());
                  res = ResponseVo.okJson(operation.getUid(),null);
                  break;
              }
              break;
            case INSERT:
            case UPDATE:
              dataBase.update(bucketName,bucket->{
                DB db = bucket;
                db.put(Iq80DBFactory.bytes(operation.getKey()),Iq80DBFactory.bytes(operation.getValue()));
              });
              res = ResponseVo.okJson(operation.getUid(),null);
              break;
            case DELETE:
              dataBase.update(bucketName,bucket->{
                DB db = bucket;
                db.delete(Iq80DBFactory.bytes(operation.getKey()));
              });
              res = ResponseVo.okJson(operation.getUid(),null);
              break;
            case SELECT:
              DB bucket = dataBase.getBucket(operation.getTarget());
              ArrayList<Map.Entry<byte[], byte[]>> dataC = new ArrayList<>();
              //todo 正则表达式
              if (operation.getKey().equals("*")){
                DBIterator iterator = bucket.iterator();
                Limit limit = operation.getLimit();
                if (limit !=null ){
                  Integer offset = limit.getOffset();
                  Integer size = limit.getSize();
                  boolean all = false;
                  if ((offset == -1)&&(size == -1)){
                    all = true;
                  }
                  int index = 0;
                  Map.Entry<byte[], byte[]> next = iterator.next();
                  while ( iterator.hasNext() && (index < (offset + size) || all) ){
                    if (index == offset){
                      dataC.add(next);
                    }
                    index++;
                  }
                }else {
                  while (iterator.hasNext()) {
                    Map.Entry<byte[], byte[]> next = iterator.next();
                    dataC.add(next);
                  }
                }
              }
              else {
                dataC.add(new AbstractMap.SimpleEntry<>(Iq80DBFactory.bytes(operation.getKey()),bucket.get(Iq80DBFactory.bytes(operation.getKey()))));
              }
              res = ResponseVo.okJson(operation.getUid(),dataC);
              break;
            case COUNT:
              //查询bucket中的数量
              DBIterator iterator = dataBase.getBucket(operation.getTarget()).iterator();
              int count = 0;
              while (iterator.hasNext()) {
                iterator.next();
                count++;
              }
              ArrayList<Object> list = new ArrayList<>();
              list.add(count);
              res = ResponseVo.okJson(operation.getUid(),list);
              break;
            default:
              throw new OptionalSyntaxException("不存在的操作命令！");
          }
          out.writeObject(res);

        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    FutureTask<String> task = new FutureTask<String>(stringCallable);
    ThreadFactory.execute(task);
    try {
      task.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }

  }

  @SneakyThrows
  public static void main(String[] args) {
    Socket socket = new Socket("127.0.0.1", 9731);
    OutputStream outputStream = socket.getOutputStream();
    outputStream.write("create database test".getBytes());
    outputStream.flush();
    ServerSocket serverSocket = new ServerSocket(1234);
    serverSocket.accept();
  }
}
