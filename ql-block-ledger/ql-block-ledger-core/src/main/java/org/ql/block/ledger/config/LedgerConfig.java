package org.ql.block.ledger.config;

import lombok.Data;

/**
 * Created at 2022/12/10 15:58
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
public class LedgerConfig {
  public static final String BLOCK_BUCKET = "blockBucket";

  public static final String CHAIN_STATE = "chainState";

  public static final String CHAIN_APPLY = "slaveChainApply";

  public static final String utxoBucket = "chainStateUTXO";



}
