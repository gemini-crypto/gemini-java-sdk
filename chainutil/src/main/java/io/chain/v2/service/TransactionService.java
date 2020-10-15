package io.chain.v2.service;
import static io.chain.v2.SDKContext.NULS_DEFAULT_OTHER_TX_FEE_PRICE;
import static io.chain.v2.constant.AccountConstant.ALIAS_FEE;

import java.math.BigInteger;
import java.util.*;

import io.chain.v2.SDKContext;
import io.chain.v2.base.AddressTool;
import io.chain.v2.base.NulsHash;
import io.chain.v2.base.Transaction;
import io.chain.v2.constant.AccountConstant;
import io.chain.v2.crypto.HexUtil;
import io.chain.v2.crypto.P2PHKSignature;
import io.chain.v2.exception.ErrorCode;
import io.chain.v2.exception.NulsException;
import io.chain.v2.model.CoinData;
import io.chain.v2.model.CoinFrom;
import io.chain.v2.model.CoinTo;
import io.chain.v2.model.Result;
import io.chain.v2.model.dto.*;
import io.chain.v2.parse.TxType;
import io.chain.v2.util.*;
public class TransactionService {
	private static TransactionService instance = new TransactionService();

    public static TransactionService getInstance() {
        return instance;
    }
    
    /**
     * 明文私钥签名交易(单签)
     *
     * @param address
     * @param txHex
     * @return
     */
    public static Result signTx(String txHex, String address, String privateKey) {
        List<SignDto> signDtoList = new ArrayList<>();
        SignDto signDto = new SignDto();
        signDto.setAddress(address);
        signDto.setPriKey(privateKey);
        signDtoList.add(signDto);
        return ChainSDKTool.sign(signDtoList, txHex);
    }
    
    public Result createTransferTx(TransferDto transferDto) {
        try {
            //CommonValidator.checkTransferDto(transferDto);
            for (CoinFromDto fromDto : transferDto.getInputs()) {
                if (fromDto.getAssetChainId() == 0) {
                    fromDto.setAssetChainId(SDKContext.main_chain_id);
                }
                if (fromDto.getAssetId() == 0) {
                    fromDto.setAssetId(SDKContext.main_asset_id);
                }
            }
            for (CoinToDto toDto : transferDto.getOutputs()) {
                if (toDto.getAssetChainId() == 0) {
                    toDto.setAssetChainId(SDKContext.main_chain_id);
                }
                if (toDto.getAssetId() == 0) {
                    toDto.setAssetId(SDKContext.main_asset_id);
                }
            }

            Transaction tx = new Transaction(TxType.TRANSFER);
            if (transferDto.getTime() != 0) {
                tx.setTime(transferDto.getTime());
            } else {
                tx.setTime(System.currentTimeMillis()/1000);
            }
            tx.setRemark(StringUtils.bytes(transferDto.getRemark()));
            CoinData coinData = assemblyCoinData(transferDto.getInputs(), transferDto.getOutputs(), tx.getSize());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (Exception e) {
        	return null;
        }
    }
    
    /**
     * 组装转账交易的coinData数据
     * Assemble the coinData for the transfer transaction
     *
     * @return coinData
     * @throws NulsException
     */
    private CoinData assemblyCoinData(List<CoinFromDto> inputs, List<CoinToDto> outputs, int txSize) throws NulsException {
        List<CoinFrom> coinFroms = new ArrayList<>();
        for (CoinFromDto from : inputs) {
            byte[] address = AddressTool.getAddress(from.getAddress());
            byte[] nonce = HexUtil.decode(from.getNonce());
            CoinFrom coinFrom = new CoinFrom(address, from.getAssetChainId(), from.getAssetId(), from.getAmount(), nonce, AccountConstant.NORMAL_TX_LOCKED);
            coinFroms.add(coinFrom);
        }
        List<CoinTo> coinTos = new ArrayList<>();
        for (CoinToDto to : outputs) {
            byte[] addressByte = AddressTool.getAddress(to.getAddress());
            CoinTo coinTo = new CoinTo(addressByte, to.getAssetChainId(), to.getAssetId(), to.getAmount(), to.getLockTime());
            coinTos.add(coinTo);
        }
        txSize = txSize + getSignatureSize(coinFroms);
        TxUtils.calcTxFee(coinFroms, coinTos, txSize);
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;
    }

    /**
     * 通过coinFroms计算签名数据的size
     * 如果coinFroms有重复地址则只计算一次
     * Calculate the size of the signature data by coinFroms
     * if coinFroms has duplicate addresses, it will only be evaluated once
     *
     * @param coinFroms 交易输入
     * @return int size
     */
    private int getSignatureSize(List<CoinFrom> coinFroms) {
        int size = 0;
        Set<String> commonAddress = new HashSet<>();
        for (CoinFrom coinFrom : coinFroms) {
            String address = AddressTool.getStringAddressByBytes(coinFrom.getAddress());
            commonAddress.add(address);
        }
        size += commonAddress.size() * P2PHKSignature.SERIALIZE_LENGTH;
        return size;
    }
    
    public BigInteger calcTransferTxFee(TransferTxFeeDto dto) {
        if (dto.getPrice() == null) {
            dto.setPrice(SDKContext.NULS_DEFAULT_NORMAL_TX_FEE_PRICE);
        }
        return TxUtils.calcTransferTxFee(dto.getAddressCount(), dto.getFromLength(), dto.getToLength(), dto.getRemark(), dto.getPrice());
    }
    
}