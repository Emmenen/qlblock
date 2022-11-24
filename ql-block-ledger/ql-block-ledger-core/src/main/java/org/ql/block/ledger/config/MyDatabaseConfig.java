package org.ql.block.ledger.config;

import org.ql.block.ledger.db.Database;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Created at 2022/6/29 20:10
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Configuration
public class MyDatabaseConfig {

  public static final String dbName = "levelDb";

  @Bean("staticDatabase")
  @DependsOn("springContextUtil")
  public Database levelDB(){
    return new Database(dbName);
  }
}

