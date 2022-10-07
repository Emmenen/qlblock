package org.ql.block.peer.utils;

import org.ql.block.peer.communication.message.MessageType;
import org.ql.block.peer.config.MsgConfig;

/**
 * Created with IntelliJ IDEA at 2022/5/18 16:40
 * User: @Qi Long
 */
public class MsgUtils {


    public static final int HEADLENGTH = MsgConfig.HEADLENGTH;

    public static MessageType getHeader(byte[] msg){
        int count = 0;
        for (int i = 0; i < HEADLENGTH; i++) {
            if (msg[i]!=0){
                count++;
            } else {
                break;
            }
        }
        byte[] header = new byte[count];

        for (int i = 0; i < count; i++){
            header[i] = msg[i];
        }
        return MessageType.valueOf(new String(header));
    }

    public static String getStringContext(byte[] msg){
        byte[] bytes = new byte[msg.length - HEADLENGTH];
        for (int i = HEADLENGTH; i < msg.length; i++) {
            bytes[i-HEADLENGTH] = msg[i];
        }
        return new String(bytes);
    }
    public static byte[] getByteContext(byte[] msg){
        byte[] bytes = new byte[msg.length - HEADLENGTH];
        for (int i = HEADLENGTH; i < msg.length; i++) {
            bytes[i-HEADLENGTH] = msg[i];
        }
        return bytes;
    }

}
