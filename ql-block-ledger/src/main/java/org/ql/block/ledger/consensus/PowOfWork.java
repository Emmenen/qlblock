package org.ql.block.ledger.consensus;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.common.annotation.RequireWallet;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.model.blockchain.BlockChain;
import org.ql.block.ledger.util.CryptoUtils;
import org.ql.block.ledger.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;

/**
 * Created at 2022/7/2 0:43
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Service("powOfWork")
@Slf4j
public class PowOfWork {

  @Autowired
  public Wallet wallet;

  @Value("${qlblock.sys.log.ismintprocess}")
  private String isMintProcess;

  //目标，求的Hash时与target进行比较
  public static BigInteger target = BigInteger.valueOf(1).shiftLeft(256-(4*6));

  public PowOfWork() {
    //todo通过类名实例化
  }

  public String prepareData(Block block){
    return block.getPreviousHash() + Integer.toHexString(block.hashCode()) + target.toString(16) + block.nonce;
  }

  /**
   * 只有在计算nonce时才用得到此方法
   * @param block
   * @param nonce
   * @return
   */
  public String prepareData(Block block,BigInteger nonce){
    block.nonce = nonce;
    /**
     * 准备数据：
     * 当一个新的区块被提交到内存中后，矿工主要先将区块中的数据进行整理，再追加nonce；
     */
    return this.prepareData(block);
  }
  @RequireWallet
  public PowOfWorkForm run(Block block) {
    BigInteger nonce = BigInteger.valueOf(0);
    PowOfWorkForm powOfWorkForm = new PowOfWorkForm();
      log.info("Mining the block containing \"{}\"", block.data.toString());
      BigInteger one = BigInteger.ONE;
      BigInteger max = one.shiftLeft(256);

      //显示挖矿进度
      String preDataStr;
      byte[] dataDigest;

      //判断是否需要打印挖矿进程；
      if (isMintProcess.equals("yes") || isMintProcess.equals("true")) {
        while (nonce.compareTo(max) < 0) {
          //todo 节点停止后，记录当前挖矿进度
          preDataStr = prepareData(block, nonce);
          System.out.print("\rprocess:" + nonce);
          dataDigest = CryptoUtils.sha256.digest(preDataStr.getBytes());
          BigInteger bigInteger = new BigInteger(dataDigest);
          if (bigInteger.abs().compareTo(target) == -1 && bigInteger.compareTo(BigInteger.ZERO) != -1) {
            String s1 = bigInteger.toString(16);
            System.out.print("\rprocess: the finally nonce is " + nonce + ", the hash which minted currently is " + s1);
            powOfWorkForm.hash = DatatypeConverter.printHexBinary(dataDigest);
            powOfWorkForm.nonce = nonce;
            powOfWorkForm.miner = wallet.getAddress();
            break;
          } else {
            nonce = nonce.add(BigInteger.ONE);
          }
        }
      } else {
        while (nonce.compareTo(max) < 0) {
          //todo 节点停止后，记录当前挖矿进度
          preDataStr = prepareData(block, nonce);
          dataDigest = CryptoUtils.sha256.digest(preDataStr.getBytes());
          BigInteger bigInteger = new BigInteger(dataDigest);
          if (bigInteger.abs().compareTo(target) == -1 && bigInteger.compareTo(BigInteger.ZERO) != -1) {
            String s1 = bigInteger.toString(16);
            System.out.print("\rMint success: the finally nonce is " + nonce + ", the hash which minted currently is " + s1);
            powOfWorkForm.hash = DatatypeConverter.printHexBinary(dataDigest);
            powOfWorkForm.nonce = nonce;
            powOfWorkForm.miner = wallet.getAddress();
            break;
          } else {
            nonce = nonce.add(BigInteger.ONE);
          }
        }
      }

    return powOfWorkForm;
  }

  /**
   * todo 为什么data一样但摘要却不一样
   * 区块的验证，主要验证区块的hash是否满足条件，并不验证交易是否与自己节点选择一致；
   * @param block
   * @return
   */
  public boolean validate(Block block){
    String data = this.prepareData(block).trim();
    String sha256Hex = CryptoUtils.encrypt_sha256_hex(data);
    System.out.println("sha256Hex"+sha256Hex);
    BigInteger bigInteger = new BigInteger(sha256Hex, 16);
    if (bigInteger.abs().compareTo(target) == -1 && bigInteger.compareTo(BigInteger.ZERO) != -1){
      return true;
    }else {
      return false;
    }
  }

  public static void main(String[] args) {
    String data = "000000A6FFE60F75B9493DF9C9F39EFCFF7F2CCE1F305BB34D4C615E1584156B20100000000000000000000000000000000000000000000000000000000002793784";
    byte[] digest = CryptoUtils.sha256.digest(data.getBytes());
    String sha256Hex = CryptoUtils.encrypt_sha256_hex(data);
    System.out.println(sha256Hex);
    BigInteger bigInteger = new BigInteger(sha256Hex, 16);
    if (bigInteger.abs().compareTo(target) == -1 && bigInteger.compareTo(BigInteger.ZERO) != -1){
      System.out.println(true);
    }else {
      System.out.println(false);
    }
    System.out.println(new BigInteger(digest));
    System.out.println(new BigInteger(digest));
  }
}
