package org.ql.block.peer;

import org.ql.block.ledger.config.SpringContextUtil;
import org.ql.block.peer.service.PeerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("org.ql.block")
@EnableCaching
public class PeerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PeerApplication.class,args);
        PeerService peerService = SpringContextUtil.getBean("peerService", PeerService.class);
        peerService.start();
    }

}
