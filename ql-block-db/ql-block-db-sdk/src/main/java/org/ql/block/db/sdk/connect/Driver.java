package org.ql.block.db.sdk.connect;

import java.io.IOException;
import java.net.Socket;

/**
 * Created at 2022/12/18 14:47
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class Driver {

  /**
   * 创建连接
   * @param ip
   * @param port
   * @return
   */
  public static Connection getConnection(String ip, int port,String dataBase) throws IOException {
    Socket socket = new Socket(ip, port);
    Connection connection = new Connection(socket,dataBase);
    return connection;
  }
}
