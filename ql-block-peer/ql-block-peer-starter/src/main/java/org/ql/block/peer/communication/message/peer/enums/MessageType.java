package org.ql.block.peer.communication.message.peer.enums;

/**
 * Created with IntelliJ IDEA at 2022/5/18 15:51
 * User: @Qi Long
 */
public enum MessageType {

    VERSION,VERSION_OK,

    GOSSIP_VERSION, GOSSIP_TRANSACTION, GOSSIP_TEXTDATA,

    NEW_ADDR_YOU,NEW_ADDR_YOU_OK,

    SET_BLOCK,GET_BLOCKS, BLOCKS_LIST, BLOCKS_OK;


    MessageType() {

    }
}
