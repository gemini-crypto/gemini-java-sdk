/*
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.chain.v2.util;
import org.apache.commons.lang.StringUtils;

import io.chain.v2.base.Address;
import io.chain.v2.constant.BaseConstant;
import io.chain.v2.crypto.ECKey;
import io.chain.v2.crypto.HexUtil;
import io.chain.v2.error.AccountErrorCode;
import io.chain.v2.exception.NulsException;
import io.chain.v2.model.Account;
import io.chain.v2.parse.SerializeUtils;
import net.sf.json.JSONObject;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import com.yzcm.common.util.MnemonicUtils;

import java.math.BigInteger;

/**
 * @author: qinyifeng
 */
public class AccountTool {
	public static void main(String[] args) {
		try {
			System.out.println(createAccount(1, "808691dc4a2bfe3483e4108542720a2353f039147e6f5025b39e45aa12a55314").getAddress().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Account createAccount(int chainId, String prikey) throws NulsException {
        ECKey key = null;
        if (StringUtils.isBlank(prikey)) {
            key = new ECKey();
        } else {
            try {
                key = ECKey.fromPrivate(new BigInteger(1, HexUtil.decode(prikey)));
            } catch (Exception e) {
                throw new RuntimeException("AccountErrorCode.PRIVATE_KEY_WRONG");
            }
        }
        Address address = new Address(chainId, BaseConstant.DEFAULT_ADDRESS_TYPE, SerializeUtils.sha256hash160(key.getPubKey()));
        Account account = new Account();
        account.setChainId(chainId);
        account.setAddress(address);
        account.setPubKey(key.getPubKey());
        account.setPriKey(key.getPrivKeyBytes());
        account.setEncryptedPriKey(new byte[0]);
        account.setCreateTime(System.currentTimeMillis());
        account.setEcKey(key);
        return account;
    }
	
	public static Account createAccount(int chainId, String prikey, String prefix) throws NulsException {
        ECKey key = null;
        if (StringUtils.isBlank(prikey)) {
            key = new ECKey();
        } else {
            try {
                key = ECKey.fromPrivate(new BigInteger(1, HexUtil.decode(prikey)));
            } catch (Exception e) {
                throw new NulsException(AccountErrorCode.PRIVATE_KEY_WRONG, e);
            }
        }
        Address address = new Address(chainId, prefix, BaseConstant.DEFAULT_ADDRESS_TYPE, SerializeUtils.sha256hash160(key.getPubKey()));
        Account account = new Account();
        account.setChainId(chainId);
        account.setAddress(address);
        account.setPubKey(key.getPubKey());
        account.setPriKey(key.getPrivKeyBytes());
        account.setEncryptedPriKey(new byte[0]);
        account.setCreateTime(System.currentTimeMillis());
        account.setEcKey(key);
        return account;
    }
	
	
	public static Account createAccount(int chainId) throws NulsException {
        return createAccount(chainId, null);
    }
	
	public static String getPrefix(String address) {
        for (int i = 1; i < address.length(); i++) {
            char c = address.charAt(i);
            if (ValidateUtil.regexMatch(c + "", "^[a-z]{1}$")) {
                return address.substring(0, i);
            }
        }
        return null;
    }
	
	public static Address createContractAddress(int chainId) {
        ECKey key = new ECKey();
        return new Address(chainId, BaseConstant.CONTRACT_ADDRESS_TYPE, SerializeUtils.sha256hash160(key.getPubKey()));
    }
	
	public static JSONObject createAddress(String mnemonic) {
		String address="";
		String pubkey="";
		String prikey="";
		try {
			byte[] seed = MnemonicUtils.generateSeed(mnemonic, null);
			DeterministicKey dkKey = HDKeyDerivation.createMasterPrivateKey(seed);
			DeterministicKey dkRoot = HDKeyDerivation.deriveChildKey(dkKey, 0);
			pubkey=dkRoot.getPublicKeyAsHex();
			prikey=dkRoot.getPrivateKeyAsHex();
			address=createAccount(1, prikey).getAddress().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject object=new JSONObject();
		object.put("address", address);
		object.put("pubkey", pubkey);
		object.put("prikey", prikey);
		return object;
	}
}
