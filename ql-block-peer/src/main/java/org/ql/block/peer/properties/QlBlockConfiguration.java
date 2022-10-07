package org.ql.block.peer.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created at 2022/10/5 21:36
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Component
@ConfigurationProperties(prefix = "qlblock")
@Data
public class QlBlockConfiguration {
  public int port;
}
