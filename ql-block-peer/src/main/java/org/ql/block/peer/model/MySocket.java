package org.ql.block.peer.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA at 2022/5/17 16:33
 * User: @Qi Long
 */
@Data
@Slf4j
public class MySocket extends Socket{
    private Socket socket;
    private String hostName;
    private int port;
    public String getHostName() {
        return socket.getInetAddress().getHostAddress();
    }

    private Peer peer;

    public Peer getPeer() {
        return peer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MySocket)) return false;

        MySocket mySocket = (MySocket) o;

        return getPeer().equals(mySocket.getPeer());
    }

    @Override
    public int hashCode() {
        return getPeer().hashCode();
    }


    @Override
    public String toString() {
        return "{" +
                "peer=" + peer +
                '}';
    }

    public int getPort(){
        return socket.getPort();
    }


    public MySocket(Peer peer) throws IOException {
        this.socket = new Socket(peer.getIp(), peer.getPort());
        this.peer = peer;
        this.hostName = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
    }

    public MySocket(Socket socket) throws IOException {
        this.socket = socket;
        this.hostName = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        this.peer = new Peer(hostName,port);
    }

    private HashMap<Object,Boolean> messageMap = new HashMap<>();

    public MyOutputStream getMyOutputStream() throws IOException {
        OutputStream outputStream;
        if (null==socket){
            outputStream = super.getOutputStream();
        }
        outputStream = socket.getOutputStream();
        return new MyOutputStream(outputStream);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (null==socket){
            return super.getOutputStream();
        }
        return socket.getOutputStream();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (null==socket){
            return super.getInputStream();
        }
        return socket.getInputStream();
    }

    public void putMessage(Object msg){
        this.messageMap.put(msg,true);
    }

    /**
     * 判断是否已经想此节点发送过该信息
     * @param msg
     * @return
     */
    public Boolean getMessageFlag(Object msg){
        Boolean aBoolean = this.messageMap.get(msg);
        if (aBoolean==null){
            aBoolean = false;
        }else {
            aBoolean = true;
        }
        return aBoolean ;
    }

}
