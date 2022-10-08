package org.ql.block.ledger.consensus;

import org.ql.block.common.annotation.RequireWallet;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.util.CryptoUtils;
import org.ql.block.ledger.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;

/**
 * Created at 2022/7/2 0:43
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Service("powOfWork")
public class PowOfWork {

  @Autowired
  public Wallet wallet;
  //目标，求的Hash时与target进行比较
  public static BigInteger target = BigInteger.valueOf(1).shiftLeft(256-(1*6));

  public PowOfWork() {
    //todo通过类名实例化
  }

  public String prepareData(Block block){
    return block.getPreviousHash() + Integer.toHexString(block.hashCode()) + target.toString(16) + Integer.toHexString(block.nonce);
  }

  /**
   * 只有在计算nonce时才用得到此方法
   * @param block
   * @param nonce
   * @return
   */
  public String prepareData(Block block,int nonce){
    block.nonce = nonce;
    /**
     * 准备数据：
     * 当一个新的区块被提交到内存中后，矿工主要先将区块中的数据进行整理，再追加nonce；
     */
    return this.prepareData(block);
  }
  @RequireWallet
  public PowOfWorkForm run(Block block){
    int nonce = 0;
    PowOfWorkForm powOfWorkForm = new PowOfWorkForm();
    System.out.printf("Mining the block containing \"%s\"\n", block.data.toString());
    while (nonce < Integer.MAX_VALUE){
      String s = prepareData(block,nonce);
      byte[] digest = CryptoUtils.sha256.digest(s.getBytes());
      BigInteger bigInteger = new BigInteger(digest);
      if (bigInteger.abs().compareTo(target) == -1 && bigInteger.compareTo(BigInteger.ZERO) != -1) {
        String s1 = bigInteger.toString(16);
        System.out.println(s1);
        System.out.println(nonce);
        powOfWorkForm.hash = DatatypeConverter.printHexBinary(digest);
        powOfWorkForm.nonce = nonce;
        powOfWorkForm.miner = wallet.getAddress();
        break;
      } else {
        nonce++;
      }
    }
    return powOfWorkForm;
  }

  public boolean validate(Block block){
    String data = this.prepareData(block);
    byte[] digest = CryptoUtils.sha256.digest(data.getBytes());
    BigInteger bigInteger = new BigInteger(digest).abs();
    if (bigInteger.compareTo(target)== -1){
      return true;
    }else {
      return false;
    }
  }
}
