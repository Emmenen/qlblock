package org.ql.block.db.sdk.connect;

import com.alibaba.fastjson.JSON;
import org.ql.block.db.share.message.Operation;
import org.ql.block.db.share.message.ResponseVo;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created at 2022/12/18 14:35
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class Connection {
  private Socket socket;
  private String dataBase;
  private ObjectOutputStream obOut;
  private ObjectInputStream obIn;

  public Connection(Socket socket,String dataBase) {
    try {
      this.socket = socket;
      this.dataBase = dataBase;
      OutputStream out = socket.getOutputStream();
      InputStream in = socket.getInputStream();
      this.obOut = new ObjectOutputStream(out);
      this.obIn = new ObjectInputStream(in);
    } catch (IOException e) {
      e.printStackTrace();
    }

    this.socket = socket;
  }

  public ResponseVo sendCommand(Operation operation){
    ResponseVo responseVo = null;
    try {
      obOut.writeObject(operation);
      String str = (String) obIn.readObject();
      responseVo = JSON.parseObject(str, ResponseVo.class);
      return responseVo;
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return ResponseVo.error(null);
  }

  public DBExecute preExecute(String option){
    return new DBExecute(this,option);
  }

  /**
   * 统一指令接口
   * @param operation
   * @return
   */
  public ResponseVo sendCommand(String operation){
    ResponseVo responseVo = null;
    try {
      obOut.write(operation.getBytes(StandardCharsets.UTF_8));
      String str = (String) obIn.readObject();
      responseVo = JSON.parseObject(str, ResponseVo.class);
      return responseVo;
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return ResponseVo.error(null);
  }

  public ResponseVo<String> simpleCommand(Operation operation) throws IOException, ClassNotFoundException {
    obOut.writeObject(operation);
    String str = (String) obIn.readObject();
    ResponseVo responseVo = JSON.parseObject(str, ResponseVo.class);
    return responseVo;
  }

  /**
   * 批量查询接口
   * @return
   */
  public ResponseVo<Map<byte[],byte[]>> selectBatch(Operation operation){
    Map<byte[],byte[]> next = null;
    ResponseVo res = null;
    try {
      obOut.writeObject(operation);
      res = (ResponseVo) obIn.readObject();
      Collection data = res.getData();
      next = (Map<byte[], byte[]>) data.iterator().next();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    ArrayList<Map<byte[], byte[]>> maps = new ArrayList<>();
    maps.add(next);
    return ResponseVo.ok(res.getMessage(),maps);
  }
}
