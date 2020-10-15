package com.yzcm.common.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.chain.v2.SDKContext;
import io.chain.v2.model.Result;
import io.chain.v2.model.dto.AccountDto;
import io.chain.v2.model.dto.CoinFromDto;
import io.chain.v2.model.dto.CoinToDto;
import io.chain.v2.model.dto.TransferDto;
import io.chain.v2.parse.JSONUtils;
import io.chain.v2.util.ChainSDKTool;
import net.sf.json.JSONObject;
public class ChainUtil {
	private final static String neknodeurl=ProperitesRead.getProperties("nodeurl");
	private final static String nekpubserverurl=ProperitesRead.getProperties("pubserverurl");
	public static void main(String[] args){
	}
	
	public static String getAccountBalance(String address) {
		String balance="0";
		try {
			Object[] str = {1,1,1,address};
			JSONObject json = new JSONObject();
			json.put("method", "getAccountBalance");
			json.put("params", str);
			json.put("id", 1234);
			json.put("jsonrpc", "2.0");
			com.alibaba.fastjson.JSONObject result =  com.alibaba.fastjson.JSONObject.parseObject(post(json));
			//System.out.println(result);
			balance=result.getJSONObject("result").getBigDecimal("balance").divide(new BigDecimal(10).pow(8)).toPlainString();
		} catch (Exception e) {
		}
		return balance;
	}

	public static String getTokenBalance(String address,String contractAddress,int decimal) {
		String balance="0"; 
		try {
			com.alibaba.fastjson.JSONArray array2=getAccontTokens(1,1,10,address);
			for (int i = 0; i < array2.size(); i++) {
				if (array2.getJSONObject(i).getString("contractAddress").equals(contractAddress)) {
					balance=array2.getJSONObject(i).getBigDecimal("balance").divide(new BigDecimal(10).pow(decimal)).toPlainString();
				}
			}
		} catch (Exception e) {
		}
		return balance;
	}
	
	/**
	 * getAccontTokens
	 * @param chainid
	 * @param pageNumber
	 * @param pageSize
	 * @param address
	 * @return
	 */
	public static com.alibaba.fastjson.JSONArray getAccontTokens(int chainid,Integer pageNumber,Integer pageSize,String address) {
		com.alibaba.fastjson.JSONArray array=new com.alibaba.fastjson.JSONArray();
		try {
			Object[] str = {chainid,pageNumber,pageSize,address};
			JSONObject json = new JSONObject();
			json.put("method", "getAccountTokens");
			json.put("params", str);
			json.put("id", 1234);
			json.put("jsonrpc", "2.0");
			com.alibaba.fastjson.JSONObject result = com.alibaba.fastjson.JSONObject.parseObject(post_publicservice(json));
			array=result.getJSONObject("result").getJSONArray("list");
		} catch (Exception e) {
		}
		return array;
	}
	
	
	public static String post_publicservice(JSONObject jsonParam) {
	 	PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(nekpubserverurl.concat("/jsonrpc"));
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.print(jsonParam.toString());
			out.flush();
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("exception！" + e);
			e.printStackTrace();
		}
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	
	public static String convertQuantity(BigDecimal number) {
		return StringUtil.subZeroAndDot(number.divide(new BigDecimal(10).pow(8)).toPlainString());
	}
	
	/**
	 * getAccountNonce
	 * @param chainid
	 * @param assetChainId
	 * @param assetId
	 * @param address
	 * @return
	 */
	public static com.alibaba.fastjson.JSONObject getAccountNonce(int chainid,int assetChainId,int assetId,String address) {
		com.alibaba.fastjson.JSONObject object=new com.alibaba.fastjson.JSONObject();
		String state="1";
		String nonce="0000000000000000";
		BigInteger balance=BigInteger.valueOf(0);
		try {
			Object[] str = {chainid,assetChainId,assetId,address};
			JSONObject json = new JSONObject();
			json.put("method", "getAccountBalance");
			json.put("params", str);
			json.put("id", 1234);
			json.put("jsonrpc", "2.0");
			com.alibaba.fastjson.JSONObject result =  com.alibaba.fastjson.JSONObject.parseObject(post(json));
			nonce=result.getJSONObject("result").getString("nonce");
			balance=result.getJSONObject("result").getBigInteger("balance");
			//System.out.println(result);
			state="0";
		} catch (Exception e) {
			e.printStackTrace();
		}
		object.put("state", state);
		object.put("nonce", nonce);
		object.put("balance", convertQuantity(new BigDecimal(balance)));
		object.put("actualbalance", balance);
		return object;
	}
	
	
	/**
	 * getBlockHeight
	 * @param chainid
	 * @return
	 */
	public static int getBlockHeight(int chainid) {
		int height=0;
		try {
			Object[] str = {chainid};
			JSONObject json = new JSONObject();
			json.put("method", "getLatestHeight");
			json.put("params", str);
			json.put("id", 1234);
			json.put("jsonrpc", "2.0");
			JSONObject result =  JSONObject.fromObject(post(json));
			System.out.println(result);
			height=result.getInt("result");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return height;
	}
	
	/**
	 * createTransferTxOffline
	 * @param chainId
	 * @param fromAddress
	 * @param toAddress
	 * @param contractAddress
	 * @param number
	 * @param remark
	 * @return
	 */
	public static JSONObject createTransferTxOffline(int chainId,String fromAddress,String toAddress,String contractAddress,BigDecimal number,BigDecimal minerfee,int decimal,String remark,String prikey) {
		String message="transfer fail";
		String state="1";
		try {
			com.alibaba.fastjson.JSONObject nonceobj=getAccountNonce(chainId, 1, 1, fromAddress);
			if (nonceobj.getString("state").equals("1")) {
				message="get nonce fail";
			}else {
				if (contractAddress.equals("")) {
					String nonce=nonceobj.getString("nonce");
					BigInteger fee=minerfee.multiply(new BigDecimal(10).pow(decimal)).toBigInteger();
			        TransferDto transferDto = new TransferDto();
			        List<CoinFromDto> inputs = new ArrayList<>();
			        CoinFromDto from = new CoinFromDto();
			        from.setAddress(fromAddress);
			        BigInteger amount=number.multiply(new BigDecimal(10).pow(decimal)).toBigInteger();
			        from.setAmount(amount.add(fee));
			        from.setAssetChainId(SDKContext.main_chain_id);
			        from.setAssetId(SDKContext.main_asset_id);
			        from.setNonce(nonce);
			        inputs.add(from);
			        List<CoinToDto> outputs = new ArrayList<>();
			        CoinToDto to = new CoinToDto();
			        to.setAddress(toAddress);
			        to.setAmount(amount);
			        to.setAssetChainId(SDKContext.main_chain_id);
			        to.setAssetId(SDKContext.main_asset_id);
			        outputs.add(to);
			        transferDto.setInputs(inputs);
			        transferDto.setOutputs(outputs);
			        //离线接口
			        Result<Map> result = ChainSDKTool.createTransferTxOffline(transferDto);
			        return privatekeySign(chainId,result,fromAddress,prikey);
				}else {
					String nonce=nonceobj.getString("nonce");
					BigInteger senderBalance=nonceobj.getBigInteger("balance");
					return tokenTransferTxOffline(chainId,fromAddress,toAddress,contractAddress,nonce,senderBalance,number,decimal,remark,prikey);
				}
			}
		} catch (Exception e) {
		}
		JSONObject object=new JSONObject();
		object.put("message", message);
		object.put("state", state);
		return object;
	}
	
	public static JSONObject privatekeySign(int chainId,Result<Map> result,String fromAddress,String prikey) {
		String message="encryp fail";
		String state="1";
		String contractAddress="";
		try {
			Map map = result.getData();
	        String txHex = (String) map.get("txHex");
	        String hash = (String) map.get("hash");
	        if (map.get("contractAddress")!=null) {
	        	contractAddress=map.get("contractAddress").toString();
			}
	        //System.out.println("txHex:"+txHex);
	        Result<Map> signTxR =ChainSDKTool.sign(txHex, fromAddress, prikey);
	        Map resultData = signTxR.getData();
	        txHex = (String) resultData.get("txHex");
	        String hash_ = (String) resultData.get("hash");
	        //System.out.println(signTxR);
	        if (hash_.equals("")) {
	        	message=txHex;
			}else {
				//System.out.println(hash+"-"+hash_);
		       // System.out.println(txHex);
		        //广播交易
		        JSONObject object=broadcastTx(chainId,txHex);
				if (object.getString("state").equals("0")) {
					state="0";
				}
				message=object.getString("message");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject object=new JSONObject();
		object.put("message", message);
		object.put("state", state);
		object.put("contractAddress", contractAddress);
		return object;
	}
	
	public static JSONObject privatekeySign1(int chainId,Result<Map> result,String fromAddress,String prikey) {
		String message="fail";
		String state="1";
		try {
			Map map = result.getData();
	        String txHex = (String) map.get("txHex");
	        String hash = (String) map.get("hash");
	        System.out.println("txHex:"+txHex);
	        Result<Map> signTxR =ChainSDKTool.sign(txHex, fromAddress, prikey);
	        Map resultData = signTxR.getData();
	        txHex = (String) resultData.get("txHex");
	        String hash_ = (String) resultData.get("hash");
	        System.out.println(signTxR);
	        if (hash_.equals("")) {
	        	message=txHex;
			}else {
				//System.out.println(hash+"-"+hash_);
		       // System.out.println(txHex);
//		        JSONObject object=broadcastTx(chainId,txHex);
//				if (object.getString("state").equals("0")) {
//					state="0";
//				}
//				message=object.getString("message");
		        state="0";
		        message=txHex;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject object=new JSONObject();
		object.put("message", message);
		object.put("state", state);
		return object;
	}
	
	public static JSONObject tokenTransferTxOffline(int chainId,String fromAddress, String toAddress, String contractAddress,String nonce,BigInteger senderBalance, BigDecimal quantity,int decimal, String remark,String prikey) {
		String message="token transfer fail";
		String state="1";
		try {
			long gasLimit = 16500;
			BigInteger amount=quantity.multiply(new BigDecimal(10).pow(decimal)).toBigInteger();
			remark = contractAddress+"_"+toAddress+"_"+amount.toString();//新增备注
	        Result<Map> result = ChainSDKTool.tokenTransferTxOffline(fromAddress, senderBalance, nonce, toAddress, contractAddress, gasLimit, amount, remark);
	        return privatekeySign(chainId,result,fromAddress,prikey);
		} catch (Exception e) {
		}
		JSONObject object=new JSONObject();
		object.put("message", message);
		object.put("state", state);
		return object;
	}
	
	public static JSONObject tokenTransferTxOffline1(int chainId,String fromAddress, String toAddress, String contractAddress,String nonce,BigInteger senderBalance, BigDecimal quantity,int decimal, String remark,String prikey) {
		String message="token transfer fail";
		String state="1";
		try {
			long gasLimit = 16500;
			BigInteger amount=quantity.multiply(new BigDecimal(10).pow(decimal)).toBigInteger();
			remark = contractAddress+"_"+toAddress+"_"+amount.toString();//新增备注
	        Result<Map> result = ChainSDKTool.tokenTransferTxOffline(fromAddress, senderBalance, nonce, toAddress, contractAddress, gasLimit, amount, remark);
	        return privatekeySign(chainId,result,fromAddress,prikey);
		} catch (Exception e) {
		}
		JSONObject object=new JSONObject();
		object.put("message", message);
		object.put("state", state);
		return object;
	}
	
	/**
	 * createAccountOffline
	 * @param count
	 * @param prefix
	 * @param password
	 * @return
	 */
	public static net.sf.json.JSONArray createAccountOffline(int count,String prefix,String password) {
		net.sf.json.JSONArray array=new net.sf.json.JSONArray();
		try {
			 Result<List<AccountDto>> result =ChainSDKTool.createOffLineAccount(count, prefix, password);
			 for (AccountDto accountDto : result.getData()) {
	            try {
	            	array.add(JSONObject.fromObject(JSONUtils.obj2json(accountDto)));
	            } catch (JsonProcessingException e) {
	                e.printStackTrace();
	            }
	        }
		} catch (Exception e) {
		}
		return array;
	}
	
	/**
	 * getPriKeyOffline
	 * @param address
	 * @param encryptedPrivateKey
	 * @param password
	 * @return
	 */
	public static String getPriKeyOffline(String address,String encryptedPrivateKey,String password) {
		String privatekey="";
		try {
			Result<Map> signTxR =ChainSDKTool.getPriKeyOffline(address, encryptedPrivateKey, password);
	        Map resultData = signTxR.getData();
	        privatekey = (String) resultData.get("priKey");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return privatekey;
	}
	
	/**
	 * importPriKey
	 * @param chainid
	 * @param priKey
	 * @param password
	 * @return
	 */
	public static String importPriKey(int chainid,String priKey,String password) {
		String address="";
		try {
			Object[] str = {chainid,priKey,password};
			JSONObject json = new JSONObject();
			json.put("method", "importPriKey");
			json.put("params", str);
			json.put("id", 1234);
			json.put("jsonrpc", "2.0");
			JSONObject result = JSONObject.fromObject(post(json));
			address=result.getString("result");
		} catch (Exception e) {
		}
		return address;
	}
	
	/**
	 * getTx
	 * @param chainid
	 * @param hash
	 * @return
	 */
	public static JSONObject getTx(int chainid,String hash) {
		String message="fail"; 
		String state="1"; 
		try {
			Object[] str = {chainid,hash};
			JSONObject json = new JSONObject();
			json.put("method", "getTx");
			json.put("params", str);
			json.put("id", 1234);
			json.put("jsonrpc", "2.0");
			String result =  post_publicservice(json);
			System.out.println(result);
			if (result.indexOf("error")!=-1) {
				message=JSONObject.fromObject(result).getJSONObject("error").getString("message");
			}else {
				message=JSONObject.fromObject(result).getString("result");
				state="0";
			}
		} catch (Exception e) {
		}
		JSONObject object=new JSONObject();
		object.put("message", message);
		object.put("state", state);
		return object;
	}
	
	public static boolean checkTx(int chainid,String hash) {
		boolean isflag=false;
		try {
			Object[] str = {chainid,hash};
			JSONObject json = new JSONObject();
			json.put("method", "getTx");
			json.put("params", str);
			json.put("id", 1234);
			json.put("jsonrpc", "2.0");
			String result =  post(json);
			if (result.indexOf("error")==-1) {
				int status=JSONObject.fromObject(result).getJSONObject("result").getInt("status");
				if (status==1) {
					isflag=true;
				}
			}
		} catch (Exception e) {
		}
		return isflag;
	}
	
	/**
	 * broadcastTx
	 * @param chainid
	 * @param tx-Transaction serialization hexadecimal string
	 * @return
	 */
	public static JSONObject broadcastTx(int chainid,String tx) {
		String message="fail"; 
		String state="1"; 
		try {
			Object[] str = {chainid,tx};
			JSONObject json = new JSONObject();
			json.put("method", "broadcastTx");
			json.put("params", str);
			json.put("id", 1234);
			json.put("jsonrpc", "2.0");
			String result =  post(json);
			System.out.println(result);
			if (result.indexOf("error")!=-1) {
				message=JSONObject.fromObject(result).getJSONObject("error").getString("message");
			}else {
				message=JSONObject.fromObject(result).getJSONObject("result").getString("hash");
				state="0";
			}
		} catch (Exception e) {
		}
		JSONObject object=new JSONObject();
		object.put("message", message);
		object.put("state", state);
		return object;
	}
	
	/**
	 * validateTx
	 * @param chainid
	 * @param tx
	 * @return
	 */
	public static JSONObject validateTx(int chainid,String tx) {
		String message="valid fail"; 
		String state="1"; 
		try {
			Object[] str = {chainid,tx};
			JSONObject json = new JSONObject();
			json.put("method", "validateTx");
			json.put("params", str);
			json.put("id", 1234);
			json.put("jsonrpc", "2.0");
			String result =  post(json);
			System.out.println(result);
			if (result.indexOf("error")!=-1) {
				message=JSONObject.fromObject(result).getJSONObject("error").getString("message");
			}else {
				message=JSONObject.fromObject(result).getJSONObject("result").getString("hash");
				state="0";
			}
		} catch (Exception e) {
		}
		JSONObject object=new JSONObject();
		object.put("message", message);
		object.put("state", state);
		return object;
	}
	
	/**
	 * getBlockByHeight
	 * @param chainid
	 * @param height
	 * @return
	 */
	public static JSONObject getBlockByHeight(int chainid,long height) {
		String message="fail"; 
		String state="1"; 
		try {
			Object[] str = {chainid,height};
			JSONObject json = new JSONObject();
			json.put("method", "getBlockByHeight");
			json.put("params", str);
			json.put("id", 1234);
			json.put("jsonrpc", "2.0");
			String result =  post(json);
			if (result.indexOf("error")!=-1) {
				message=JSONObject.fromObject(result).getJSONObject("error").getString("message");
			}else {
				message=JSONObject.fromObject(result).getJSONObject("result").toString();
				state="0";
			}
		} catch (Exception e) {
		}
		JSONObject object=new JSONObject();
		object.put("message", message);
		object.put("state", state);
		return object;
	}
	
	/**
	 * tokentransfer
	 * @param chainid		
	 * @param fromAddres
	 * @param password
	 * @param toAddress	
	 * @param contractAddres
	 * @param number
	 * @param remark
	 * @return
	 */
	public static JSONObject tokentransfer(int chainid,String fromAddress,String password,String toAddress,String contractAddress,BigDecimal number,int decimal,String remark) {
		String message="contract transfer fail"; 
		String state="1"; 
		try {
			BigInteger amount=number.multiply(new BigDecimal(10).pow(decimal)).toBigInteger();
			System.out.println(amount);
			Object[] str = {chainid,fromAddress,password,toAddress,contractAddress,amount,remark};
			JSONObject json = new JSONObject();
			json.put("method", "tokentransfer");
			json.put("params", str);
			json.put("id", 1234);
			json.put("jsonrpc", "2.0");
			String result =  post(json);
			System.out.println(result);
			if (result.indexOf("error")!=-1) {
				message=JSONObject.fromObject(result).getJSONObject("error").getString("message");
			}else {
				message="success";
				state="0";
			}
		} catch (Exception e) {
		}
		JSONObject object=new JSONObject();
		object.put("message", message);
		object.put("state", state);
		return object;
	}
	
	public static String post(JSONObject jsonParam) {
	 	PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(neknodeurl.concat("/jsonrpc"));
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.print(jsonParam.toString());
			out.flush();
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("fail！" + e);
			e.printStackTrace();
		}
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
}