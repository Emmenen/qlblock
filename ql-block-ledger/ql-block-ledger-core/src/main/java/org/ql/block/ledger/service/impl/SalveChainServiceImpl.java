package org.ql.block.ledger.service.impl;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.ql.block.common.config.SpringContextUtil;
import org.ql.block.ledger.communication.salve.SalveApply;
import org.ql.block.ledger.model.block.SalveBlock;
import org.ql.block.ledger.model.blockchain.SalveBlockChain;
import org.ql.block.ledger.model.blockchain.SalveBlockChainStatus;
import org.ql.block.ledger.service.DatabaseService;
import org.ql.block.ledger.service.SalveChainService;
import org.ql.block.ledger.util.CryptoUtils;
import org.ql.block.ledger.util.ObjectUtil;
import org.ql.block.ledger.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;

import static org.ql.block.ledger.config.LedgerConfig.*;

/**
 * Created at 2022/12/10 15:54
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Service
public class SalveChainServiceImpl implements SalveChainService {

  @Autowired
  private DatabaseService databaseService;

  /**
   * 发起一个新的从链的申请
   * @return 返回从链的创世区块
   */
  public SalveBlock applyForNewSalve(SalveBlockChain salveBlockChain){
    salveBlockChain.setStatus(SalveBlockChainStatus.APPLY);
    KeyPair keyPair = CryptoUtils.newKeyPair();
    Wallet wallet = SpringContextUtil.getBean("wallet", Wallet.class);
    SalveBlock salveBlock = SalveBlock.newGenesisSalve(keyPair.getPublic(), wallet.getUser().getPublicKey());
    databaseService.createBucket(CHAIN_STATE);


    DB bucket = salveBlockChain.databaseImpl.createIfNotExistBuket(CHAIN_STATE);
    /**
     * 首先从世界状态中取出申请
     */
    Object o = databaseService.selectOne(CHAIN_STATE, CHAIN_APPLY);
    SalveApply salveApply = ObjectUtil.byteArrayToObject(bucket.get(Iq80DBFactory.bytes(CHAIN_APPLY)), SalveApply.class);
    /**
     * 将新的申请添加到申请中
     */
    salveApply.addApply(salveBlock);
    bucket.put(Iq80DBFactory.bytes(CHAIN_APPLY),ObjectUtil.ObjectToByteArray(salveApply));
    return salveBlock;
  }


}
