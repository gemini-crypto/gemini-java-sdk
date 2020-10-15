package com.yzcm.system.service;
import java.math.BigDecimal;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import com.yzcm.common.util.ChainUtil;
import com.yzcm.common.util.ProperitesRead;
import com.yzcm.common.util.Utils;

import io.chain.v2.util.AccountTool;
import net.sf.json.JSONObject;
@Component
@Configurable
@EnableScheduling
public class WalletService{
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	public String createAddress(String mnemonic) {
		return AccountTool.createAddress(mnemonic).toString();
	}
	
	public String getBalances(String address,String contractAddress) {
		String msg="fail";
		String state="1";
		String balance="0";
		String tokenbalance="0";
		try {
			balance=ChainUtil.getAccountBalance(address);
			if (!contractAddress.equals("")) {
				tokenbalance=ChainUtil.getTokenBalance(address, contractAddress, 8);
			}
			msg="success";
			state="0";
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject object=new JSONObject();
		object.put("msg", msg);
		object.put("state", state);
		object.put("balance", balance);
		object.put("tokenbalance", tokenbalance);
		return object.toString();
	}
	
	public String transfer(String toaddress,String privatekey,String number) {
		String msg="send fail";
		String state="1";
		String hash="";
		try {
			if (Utils.isNumeric(number)==false) {
				msg="Illegal quantity";
			}else {
				if (validGemAddress(toaddress)==false) {
					msg="Illegal acceptance address";
				}else {
					String fromaddress=AccountTool.createAccount(1, privatekey).getAddress().toString();
					BigDecimal quantity=new BigDecimal(number);
					String contractAddress="";
					JSONObject obj=ChainUtil.createTransferTxOffline(1,fromaddress,toaddress,contractAddress,quantity,BigDecimal.valueOf(0.001),8,"",privatekey);
					if (obj.getString("state").equals("0")) {
						msg="success";
						hash=obj.getString("message");
						state="0";
					}else {
						msg=obj.getString("message");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Utils.toJSON(msg, hash, state);
	}
	
	public String tokenTransfer(String toaddress,String contractAddress,String privatekey,String number) {
		String msg="send fail";
		String state="1";
		String hash="";
		try {
			if (Utils.isNumeric(number)==false) {
				msg="Illegal quantity";
			}else {
				if (validGemAddress(toaddress)==false) {
					msg="Illegal acceptance address";
				}else {
					String fromaddress=AccountTool.createAccount(1, privatekey).getAddress().toString();
					BigDecimal quantity=new BigDecimal(number);
					com.alibaba.fastjson.JSONObject nonceobj=ChainUtil.getAccountNonce(1, 1, 1, fromaddress);
					if (nonceobj.getString("state").equals("1")) {
						 msg="get nonce fail";
					}else {
						BigDecimal tokenbalance=new BigDecimal(ChainUtil.getTokenBalance(fromaddress, contractAddress, 8));
						if (tokenbalance.compareTo(quantity)==-1) {
							msg="token Insufficient funds";
						}else {
							JSONObject resultObj=ChainUtil.tokenTransferTxOffline(1,fromaddress,toaddress,contractAddress,nonceobj.getString("nonce"),nonceobj.getBigInteger("actualbalance"),quantity,8,"",privatekey);
							if (resultObj.getString("state").equals("0")) {
								msg="success";
								state="0";
								hash=resultObj.getString("message");
							}else {
								msg=resultObj.getString("message");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Utils.toJSON(msg, hash, state);
	}
	
	 public static boolean validGemAddress(String address) {
        boolean isflag = false;
        String regex = "^("+ProperitesRead.getProperties("prefix")+")?[0-9a-zA-Z]{33}$";
        if (Pattern.matches(regex, address)) {
            isflag = true;
        }
        return isflag;
    }
}