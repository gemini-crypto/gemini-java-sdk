package io.chain.v2.service;

import static io.chain.v2.constant.Constant.CONTRACT_MINIMUM_PRICE;
import static io.chain.v2.constant.Constant.MAX_GASLIMIT;
import static io.chain.v2.error.AccountErrorCode.ADDRESS_ERROR;
import static io.chain.v2.error.ContractErrorCode.CONTRACT_ALIAS_FORMAT_ERROR;
import static io.chain.v2.exception.CommonCodeConstanst.NULL_PARAMETER;
import static io.chain.v2.util.ContractUtil.getFailed;
import static io.chain.v2.util.ContractUtil.getSuccess;
import static io.chain.v2.util.ValidateUtil.validateChainId;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.chain.v2.SDKContext;
import io.chain.v2.base.Address;
import io.chain.v2.base.AddressTool;
import io.chain.v2.base.Transaction;
import io.chain.v2.constant.Constant;
import io.chain.v2.crypto.HexUtil;
import io.chain.v2.error.ContractErrorCode;
import io.chain.v2.exception.CommonCodeConstanst;
import io.chain.v2.exception.ErrorCode;
import io.chain.v2.model.FormatValidUtils;
import io.chain.v2.model.Result;
import io.chain.v2.parse.MapUtils;
import io.chain.v2.txdata.CallContractData;
import io.chain.v2.txdata.CreateContractData;
import io.chain.v2.util.AccountTool;
import io.chain.v2.util.ContractUtil;
import io.chain.v2.util.StringUtils;

/**
 * @author: PierreLuo
 * @date: 2019-07-01
 */
public class ContractService {

    private static ContractService instance = new ContractService();

    public static ContractService getInstance() {
        return instance;
    }
    
    /**
     *  @Parameter(parameterName = "fromAddress", parameterDes = "转出者账户地址"),
            @Parameter(parameterName = "senderBalance", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出者账户余额"),
            @Parameter(parameterName = "nonce", parameterDes = "转出者账户nonce值"),
            @Parameter(parameterName = "toAddress", parameterDes = "转入者账户地址"),
            @Parameter(parameterName = "contractAddress", parameterDes = "token合约地址"),
            @Parameter(parameterName = "gasLimit", requestType = @TypeDescriptor(value = long.class), parameterDes = "设置合约执行消耗的gas上限"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出的token资产金额"),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
     *
     * @return
     */
    public Result<Map> tokenTransferTxOffline(String fromAddress, BigInteger senderBalance, String nonce, String toAddress, String contractAddress, long gasLimit, BigInteger amount, String remark) {
        int chainId = SDKContext.main_chain_id;
        if (amount == null || amount.compareTo(BigInteger.ZERO) <= 0) {
            return Result.getFailed(ContractErrorCode.PARAMETER_ERROR).setMsg(String.format("amount [%s] is invalid", amount));
        }

        if (!AddressTool.validAddress(chainId, fromAddress)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("fromAddress [%s] is invalid", fromAddress));
        }

        if (!AddressTool.validAddress(chainId, toAddress)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("toAddress [%s] is invalid", toAddress));
        }

        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("contractAddress [%s] is invalid", contractAddress));
        }
        return this.callContractTxOffline(fromAddress, senderBalance, nonce, null, contractAddress, gasLimit, Constant.NRC20_METHOD_TRANSFER, null,
                new Object[]{toAddress, amount.toString()}, new String[]{"String", "BigInteger"}, remark);
    }
    
    /**
     * 离线组装 - 调用合约的交易
     * @Parameter(parameterName = "sender", parameterDes = "交易创建者账户地址"),
            @Parameter(parameterName = "senderBalance", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "账户余额"),
            @Parameter(parameterName = "nonce", parameterDes = "账户nonce值"),
            @Parameter(parameterName = "value", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "调用者向合约地址转入的主网资产金额，没有此业务时填BigInteger.ZERO"),
            @Parameter(parameterName = "contractAddress", parameterDes = "合约地址"),
            @Parameter(parameterName = "gasLimit", requestType = @TypeDescriptor(value = long.class), parameterDes = "设置合约执行消耗的gas上限"),
            @Parameter(parameterName = "methodName", parameterDes = "合约方法"),
            @Parameter(parameterName = "methodDesc", parameterDes = "合约方法描述，若合约内方法没有重载，则此参数可以为空", canNull = true),
            @Parameter(parameterName = "args", requestType = @TypeDescriptor(value = Object[].class), parameterDes = "参数列表", canNull = true),
            @Parameter(parameterName = "argsType", requestType = @TypeDescriptor(value = String[].class), parameterDes = "参数类型列表", canNull = true),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
     * @return
     */
    
    public Result<Map> callContractTxOffline(String sender, BigInteger senderBalance, String nonce, BigInteger value, String contractAddress, long gasLimit,
                                     String methodName, String methodDesc, Object[] args, String[] argsType, String remark) {
        int chainId = SDKContext.main_chain_id;
        if (!AddressTool.validAddress(chainId, sender)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("sender [%s] is invalid", sender));
        }

        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("contractAddress [%s] is invalid", contractAddress));
        }

        if (StringUtils.isBlank(methodName)) {
            return Result.getFailed(NULL_PARAMETER).setMsg("methodName is empty");
        }
        if (value == null) {
            value = BigInteger.ZERO;
        }

        int assetChainId = SDKContext.nuls_chain_id;
        int assetId = SDKContext.nuls_asset_id;
        // 生成参数的二维数组
        String[][] finalArgs = null;
        if (args != null && args.length > 0) {
            if(argsType == null || argsType.length != args.length) {
                return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("size of 'argsType' array not match 'args' array");
            }
            finalArgs = ContractUtil.twoDimensionalArray(args, argsType);
        }
        // 组装交易的txData
        byte[] contractAddressBytes = AddressTool.getAddress(contractAddress);
        byte[] senderBytes = AddressTool.getAddress(sender);
        CallContractData callContractData = new CallContractData();
        callContractData.setContractAddress(contractAddressBytes);
        callContractData.setSender(senderBytes);
        callContractData.setValue(value);
        callContractData.setPrice(CONTRACT_MINIMUM_PRICE);
        callContractData.setGasLimit(gasLimit);
        callContractData.setMethodName(methodName);
        callContractData.setMethodDesc(methodDesc);
        if (finalArgs != null) {
            callContractData.setArgsCount((short) finalArgs.length);
            callContractData.setArgs(finalArgs);
        }
        // 生成交易
        Transaction tx = ContractUtil.newCallTx(chainId, assetId, senderBalance, nonce, callContractData, remark);
        try {
            Map<String, Object> resultMap = new HashMap<>(4);
            resultMap.put("hash", tx.getHash().toHex());
            resultMap.put("txHex", HexUtil.encode(tx.serialize()));
            return ContractUtil.getSuccess().setData(resultMap);
        } catch (IOException e) {
            return ContractUtil.getFailed().setMsg(e.getMessage());
        }
    }
    
    public Result<Map> createContractTxOffline(String sender, BigInteger senderBalance, String nonce, String alias, String contractCode, long gasLimit, Object[] args, String[] argsType, String remark) {
        int chainId = SDKContext.main_chain_id;
        if (!AddressTool.validAddress(chainId, sender)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("sender [%s] is invalid", sender));
        }
        if (!FormatValidUtils.validAlias(alias)) {
            return Result.getFailed(CONTRACT_ALIAS_FORMAT_ERROR).setMsg(String.format("alias [%s] is invalid", alias));
        }
        if (StringUtils.isBlank(contractCode)) {
            return Result.getFailed(CommonCodeConstanst.NULL_PARAMETER).setMsg("contractCode is empty");
        }

        int assetChainId = SDKContext.nuls_chain_id;
        int assetId = SDKContext.nuls_asset_id;

        // 随机生成一个合约地址
        Address contract = AccountTool.createContractAddress(chainId);
        byte[] contractAddressBytes = contract.getAddressBytes();
        // 生成参数的二维数组
        String[][] finalArgs = null;
        if (args != null && args.length > 0) {
            if(argsType == null || argsType.length != args.length) {
                return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("size of 'argsType' array not match 'args' array");
            }
            finalArgs = ContractUtil.twoDimensionalArray(args, argsType);
        }
        // 组装交易的txData
        byte[] contractCodeBytes = HexUtil.decode(contractCode);
        byte[] senderBytes = AddressTool.getAddress(sender);
        CreateContractData createContractData = new CreateContractData();
        createContractData.setSender(senderBytes);
        createContractData.setContractAddress(contractAddressBytes);
        createContractData.setGasLimit(gasLimit);
        createContractData.setPrice(CONTRACT_MINIMUM_PRICE);
        createContractData.setCode(contractCodeBytes);
        createContractData.setAlias(alias);
        if (finalArgs != null) {
            createContractData.setArgsCount((short) finalArgs.length);
            createContractData.setArgs(finalArgs);
        }
        // 生成交易
        Transaction tx = ContractUtil.newCreateTx(chainId, assetId, senderBalance, nonce, createContractData, remark);
        try {
            Map<String, Object> resultMap = new HashMap<>(4);
            resultMap.put("hash", tx.getHash().toHex());
            resultMap.put("txHex", HexUtil.encode(tx.serialize()));
            resultMap.put("contractAddress", AddressTool.getStringAddressByBytes(contractAddressBytes));
            return getSuccess().setData(resultMap);
        } catch (IOException e) {
            return getFailed().setMsg(e.getMessage());
        }
    }
}