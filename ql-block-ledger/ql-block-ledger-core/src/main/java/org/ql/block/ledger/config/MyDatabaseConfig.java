package org.ql.block.ledger.config;

import org.ql.block.db.sdk.connect.Connection;
import org.ql.block.db.sdk.connect.Driver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.IOException;

/**
 * Created at 2022/6/29 20:10
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Configuration
public class MyDatabaseConfig {

  @Value("${qlblock.database.port}")
  private int port;

  @Value("${qlblock.database.ip}")
  private String ip;

  @Value("${qlblock.database.name}")
  private String name;


  @Bean("dbConnection")
  public Connection getDBConnection(){
    Connection connection = null;
    try {
      connection = Driver.getConnection(ip, port,name);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return connection;
  }
}

