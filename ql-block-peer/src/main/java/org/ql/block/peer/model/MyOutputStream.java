package org.ql.block.peer.model;

import org.ql.block.ledger.util.ObjectUtil;
import org.ql.block.peer.communication.message.peer.MessageVO;
import org.ql.block.peer.config.MsgConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * 自定义输出流，当套接字再进行传输的时候必须进行统一操作
 * Created with IntelliJ IDEA at 2022/5/18 15:49
 * User: @Qi Long
 */
public class MyOutputStream extends OutputStream {
    public static final int HEADLENGTH = MsgConfig.HEADLENGTH;
    protected OutputStream outputStream;

    public MyOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Deprecated
    public void write(int b) throws IOException {
        outputStream.write(b);
    }

    public void write(MessageVO messageVO) throws IOException {
        outputStream.write(Objects.requireNonNull(ObjectUtil.ObjectToByteArray(messageVO)));
    }

//
//    public void write(MessageType messageHead,byte[] bytes) throws IOException {
//        byte[] head = messageHead.toString().getBytes(StandardCharsets.UTF_8);
//        byte[] msg = mergeByte(head, bytes);
//        outputStream.write(msg);
//    }
//
//    public void write(MessageType messageType,int b) throws IOException {
//        byte[] head = messageType.toString().getBytes(StandardCharsets.UTF_8);
//        write(mergeByte(head, (byte) b));
//    }
    public static byte[] mergeByte(byte[] bytesFormer,byte b){
        int length = bytesFormer.length;
        byte[] bytes = new byte[HEADLENGTH + 1];
        for (int i = 0; i < length; i++) {
            bytes[i] = bytesFormer[i];
        }
        bytes[HEADLENGTH] = b;
        return bytes;
    }



    public static byte[] mergeByte(byte[] bytesFormer,byte[] bytesLater){
        int len1 = bytesFormer.length;
        int len2 = bytesLater.length;

        byte[] bytes = new byte[len1 + len2 + 1];
        for (int i = 0; i < len1; i++) {
            bytes[i] = bytesFormer[i];
        }
        for (int i = 0; i < len2; i++) {
            bytes[HEADLENGTH] = bytesLater[i];
        }
        return bytes;
    }
}
