package org.ql.block.peer.communication.message;

/**
 * Created with IntelliJ IDEA at 2022/5/18 15:51
 * User: @Qi Long
 */
public enum MessageType {

    VERSION,VERSION_OK,

    GOSSIP_VERSION,

    NEW_ADDR_YOU,NEW_ADDR_YOU_OK,

    GET_BLOCKS, BLOCKS_LIST, BLOCKS_OK;


    MessageType() {
    }
}
