package com.yzcm.system.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.yzcm.system.service.WalletService;
@Controller
@RequestMapping("/wallet/")
public class WalletController extends BaseController {
	
	@Autowired
	private WalletService walletService;
	
	
	/**
	 * getBalances
	 * @param words
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="getBalances",produces="application/json;charset=UTF-8")
	public Object getBalances(String address,String contractAddress) throws Exception {
		return walletService.getBalances(address,contractAddress);
	}
	
	
	/**
	 * createAddress
	 * @param words
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="createAddress",produces="application/json;charset=UTF-8")
	public Object createAddress(String words) throws Exception {
		return walletService.createAddress(words);
	}
	
	/**
	 * transfer
	 * @param toaddress
	 * @param privatekey
	 * @param number
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="transfer",produces="application/json;charset=UTF-8")
	public Object transfer(String toaddress,String privatekey,String number) throws Exception {
		return walletService.transfer(toaddress,privatekey,number);
	}
	
	/**
	 * tokenTransfer
	 * @param toaddress
	 * @param contractAddress
	 * @param privatekey
	 * @param number
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="tokenTransfer",produces="application/json;charset=UTF-8")
	public Object tokenTransfer(String toaddress,String contractAddress,String privatekey,String number) throws Exception {
		return walletService.tokenTransfer(toaddress,contractAddress,privatekey,number);
	}
}