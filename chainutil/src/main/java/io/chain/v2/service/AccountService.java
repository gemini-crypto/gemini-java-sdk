package io.chain.v2.service;
import static io.chain.v2.util.ValidateUtil.validateChainId;

import java.io.IOException;
import java.util.*;
import org.apache.commons.lang.StringUtils;

import io.chain.v2.SDKContext;
import io.chain.v2.base.AddressTool;
import io.chain.v2.base.NulsByteBuffer;
import io.chain.v2.base.Transaction;
import io.chain.v2.crypto.AESEncrypt;
import io.chain.v2.crypto.ECKey;
import io.chain.v2.crypto.HexUtil;
import io.chain.v2.crypto.SignatureUtil;
import io.chain.v2.error.AccountErrorCode;
import io.chain.v2.exception.CryptoException;
import io.chain.v2.exception.NulsException;
import io.chain.v2.exception.NulsRuntimeException;
import io.chain.v2.model.Account;
import io.chain.v2.model.FormatValidUtils;
import io.chain.v2.model.Result;
import io.chain.v2.model.dto.AccountDto;
import io.chain.v2.model.dto.SignDto;
import io.chain.v2.util.AccountTool;
import io.chain.v2.util.CommonValidator;
public class AccountService {

    private AccountService() {

    }

    private static AccountService instance = new AccountService();

    public static AccountService getInstance() {
        return instance;
    }
    
    /**
     * sign the tx's digest
     *
     * @param signDtoList 签名请求参数
     * @return result
     */
    public Result sign(List<SignDto> signDtoList, String txHex) {
    	validateChainId();
        try {
            CommonValidator.validateSignDto(signDtoList);
            if (StringUtils.isBlank(txHex)) {
                throw new NulsRuntimeException(AccountErrorCode.PARAMETER_ERROR, "txHex is invalid");
            }
            List<ECKey> signEcKeys = new ArrayList<>();
            for (SignDto signDto : signDtoList) {
                byte[] priKeyBytes;
                if (StringUtils.isNotBlank(signDto.getPriKey())) {
                    if (!ECKey.isValidPrivteHex(signDto.getPriKey())) {
                        throw new NulsRuntimeException(AccountErrorCode.PRIVATE_KEY_WRONG, signDto.getPriKey() + " is invalid");
                    }
                    priKeyBytes = HexUtil.decode(signDto.getPriKey());

                } else {
                    try {
                        priKeyBytes = AESEncrypt.decrypt(HexUtil.decode(signDto.getEncryptedPrivateKey()), signDto.getPassword());
                        if (!ECKey.isValidPrivteHex(HexUtil.encode(priKeyBytes))) {
                            throw new NulsRuntimeException(AccountErrorCode.PRIVATE_KEY_WRONG, signDto.getEncryptedPrivateKey() + " is invalid");
                        }
                    } catch (CryptoException e) {
                        throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "encryptedPrivateKey[" + signDto.getEncryptedPrivateKey() + "] password error");
                    }
                }
                Account account = AccountTool.createAccount(SDKContext.main_chain_id, HexUtil.encode(priKeyBytes));
                String validaddress=account.getAddress().getBase58();
                //System.out.println(signDto.getAddress()+"-"+validaddress);
                if (!signDto.getAddress().equals(validaddress)) {
                	 Map<String, Object> map = new HashMap<>();
                     map.put("hash", "");
                     map.put("txHex", "私钥有误");
                     return Result.getSuccess(map);
                }
                ECKey ecKey = account.getEcKey(signDto.getPassword());
                signEcKeys.add(ecKey);
            }
            Transaction tx = new Transaction();
            tx.parse(new NulsByteBuffer(HexUtil.decode(txHex)));
            SignatureUtil.createTransactionSignture(tx, signEcKeys);

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.SERIALIZE_ERROR).setMsg(AccountErrorCode.SERIALIZE_ERROR.getMsg());
        }
    }
    
    public Result<List<AccountDto>> createOffLineAccount(int count, String prefix, String password) {
        validateChainId();

        List<AccountDto> list = new ArrayList<>();
        try {
            if (!FormatValidUtils.validPassword(password)) {
                throw new NulsException(AccountErrorCode.PASSWORD_FORMAT_WRONG);
            }
            if (count < 1) {
                count = 1;
            }
            for (int i = 0; i < count; i++) {
                //create account
                Account account;
                if (StringUtils.isBlank(prefix)) {
                    account = AccountTool.createAccount(SDKContext.main_chain_id);
                } else {
                    account = AccountTool.createAccount(SDKContext.main_chain_id, null, prefix);
                }
                if (StringUtils.isNotBlank(password)) {
                    account.encrypt(password);
                }
                AccountDto accountDto = new AccountDto();
                accountDto.setAddress(account.getAddress().toString());
                accountDto.setPubKey(HexUtil.encode(account.getPubKey()));
                if (account.isEncrypted()) {
                    accountDto.setPrikey("");
                    accountDto.setEncryptedPrivateKey(HexUtil.encode(account.getEncryptedPriKey()));
                } else {
                    accountDto.setPrikey(HexUtil.encode(account.getPriKey()));
                    accountDto.setEncryptedPrivateKey("");
                }
                list.add(accountDto);
            }
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
        return Result.getSuccess(list);
    }
    
    /**
     * get unencrypted private-key
     * 获取原始私钥
     *
     * @param address         账户地址
     * @param encryptedPriKey 账户加密后的私钥
     * @param password        密码
     * @return Result
     */
    public Result getPriKeyOffline(String address, String encryptedPriKey, String password) {
        validateChainId();
        try {
            if (!AddressTool.validAddress(SDKContext.main_chain_id, address)) {
                throw new NulsException(AccountErrorCode.ADDRESS_ERROR);
            }
            if (StringUtils.isBlank(encryptedPriKey)) {
                throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "encryptedPriKey is invalid");
            }
            if (!FormatValidUtils.validPassword(password)) {
                throw new NulsException(AccountErrorCode.PASSWORD_FORMAT_WRONG);
            }
            byte[] priKeyBytes = AESEncrypt.decrypt(HexUtil.decode(encryptedPriKey), password);
            if (!ECKey.isValidPrivteHex(HexUtil.encode(priKeyBytes))) {
                throw new NulsException(AccountErrorCode.PRIVATE_KEY_WRONG);
            }
            Account account = AccountTool.createAccount(SDKContext.main_chain_id, HexUtil.encode(priKeyBytes));
            if (!address.equals(account.getAddress().getBase58())) {
                throw new NulsException(AccountErrorCode.ADDRESS_ERROR);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("priKey", HexUtil.encode(account.getPriKey()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (CryptoException e) {
            return Result.getFailed(AccountErrorCode.PASSWORD_IS_WRONG).setMsg(AccountErrorCode.PASSWORD_IS_WRONG.getMsg());
        }
    }
}