package org.ql.block.peer.context;

import org.ql.block.common.config.properties.QlBlockConfiguration;
import org.ql.block.peer.model.MyServerSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA at 2022/5/18 14:51
 * User: @Qi Long
 */
@Configuration
@Slf4j
public class MySocketConfig {

    @Autowired
    private QlBlockConfiguration qlBlockConfiguration;

    @Bean
    public MyServerSocket myServerSocket() throws IOException {
        MyServerSocket myServerSocket = new MyServerSocket(qlBlockConfiguration.getPort());
        log.info("peer started at port:"+qlBlockConfiguration.getPort());
        return myServerSocket;
    }
}
