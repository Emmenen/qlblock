package org.ql.block.peer.handler;

import org.apache.http.HttpStatus;
import org.ql.block.peer.context.PeerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.SocketException;

/**
 * Created with IntelliJ IDEA at 2022/5/18 19:14
 * User: @Qi Long
 */
@Configuration
@RestControllerAdvice
public class ExceptionHanlder {


    @ExceptionHandler(SocketException.class)
    public ResponseEntity<String> bindExceptionHandler(SocketException e){
        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(e.getMessage());
    }
}
