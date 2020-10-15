package io.chain.v2.util;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import io.chain.v2.model.Result;
import io.chain.v2.model.annotation.ApiOperation;
import io.chain.v2.model.dto.*;
import io.chain.v2.service.*;
public class ChainSDKTool {
    private static AccountService accountService = AccountService.getInstance();
   
    private static TransactionService transactionService = TransactionService.getInstance();
    
    private static ContractService contractService = ContractService.getInstance();
	
	public static Result sign(List<SignDto> signDtoList, String txHex) {
        return accountService.sign(signDtoList, txHex);
    }
	
	public static Result sign(String txHex, String address, String privateKey) {
        return transactionService.signTx(txHex, address, privateKey);
    }
	
	@ApiOperation(description = "主网币组装离线交易", order = 152)
	public static Result createTransferTxOffline(TransferDto transferDto) {
        return transactionService.createTransferTx(transferDto);
    }
	
	public static Result<List<AccountDto>> createOffLineAccount(int count, String password) {
        return accountService.createOffLineAccount(count, null, password);
    }
	
	public static BigInteger calcTransferTxFee(TransferTxFeeDto dto) {
        return transactionService.calcTransferTxFee(dto);
    }
	
	@ApiOperation(description = "离线 - 批量创建地址带固定前缀的账户", order = 151, detailDesc = "创建的账户不会保存到钱包中,接口直接返回账户的keystore信息")
    public static Result<List<AccountDto>> createOffLineAccount(int count, String prefix, String password) {
        return accountService.createOffLineAccount(count, prefix, password);
    }
	
	@ApiOperation(description = "离线获取账户明文私钥", order = 153)
	public static Result getPriKeyOffline(String address, String encryptedPriKey, String password) {
        return accountService.getPriKeyOffline(address, encryptedPriKey, password);
    }
	
	@ApiOperation(description = "离线组装 - token转账交易", order = 454)
	public static Result<Map> tokenTransferTxOffline(String fromAddress, BigInteger senderBalance, String nonce, String toAddress, String contractAddress, long gasLimit, BigInteger amount, String remark) {
        return contractService.tokenTransferTxOffline(fromAddress, senderBalance, nonce, toAddress, contractAddress, gasLimit, amount, remark);
    }
	
	@ApiOperation(description = "离线组装 - 发布合约的交易", order = 451)
	public static Result<Map> createContractTxOffline(String sender, BigInteger senderBalance, String nonce, String alias, String contractCode, long gasLimit, Object[] args, String[] argsType, String remark) {
        return contractService.createContractTxOffline(sender, senderBalance, nonce, alias, contractCode, gasLimit, args, argsType, remark);
    }
}