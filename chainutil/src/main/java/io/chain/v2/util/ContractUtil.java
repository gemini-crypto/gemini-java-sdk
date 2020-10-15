/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2019 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.chain.v2.util;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import io.chain.v2.base.NulsData;
import io.chain.v2.base.VarInt;
import io.chain.v2.constant.Constant;
import io.chain.v2.exception.CommonCodeConstanst;
import io.chain.v2.model.CoinData;
import io.chain.v2.model.CoinFrom;
import io.chain.v2.model.CoinTo;
import io.chain.v2.model.Result;
import io.chain.v2.txdata.CallContractData;
import io.chain.v2.txdata.CallContractTransaction;
import io.chain.v2.txdata.ContractData;
import io.chain.v2.txdata.CreateContractData;
import io.chain.v2.txdata.CreateContractTransaction;

import java.lang.reflect.Array;

/**
 * @author: PierreLuo
 * @date: 2018/8/25
 */
public class ContractUtil {
	public static String[][] twoDimensionalArray(Object[] args, String[] types) {
        if (args == null) {
            return null;
        } else {
            int length = args.length;
            String[][] two = new String[length][];
            Object arg;
            for (int i = 0; i < length; i++) {
                arg = args[i];
                if (arg == null) {
                    two[i] = new String[0];
                    continue;
                }
                if (arg instanceof String) {
                    String argStr = (String) arg;
                    // 非String类型参数，若传参是空字符串，则赋值为空一维数组，避免数字类型转化异常 -> 空字符串转化为数字
                    if (types != null && StringUtils.isBlank(argStr) && !Constant.STRING.equalsIgnoreCase(types[i])) {
                        two[i] = new String[0];
                    } else {
                        two[i] = new String[]{argStr};
                    }
                } else if (arg.getClass().isArray()) {
                    int len = Array.getLength(arg);
                    String[] result = new String[len];
                    for (int k = 0; k < len; k++) {
                        result[k] = valueOf(Array.get(arg, k));
                    }
                    two[i] = result;
                } else if (arg instanceof ArrayList) {
                    ArrayList resultArg = (ArrayList) arg;
                    int size = resultArg.size();
                    String[] result = new String[size];
                    for (int k = 0; k < size; k++) {
                        result[k] = valueOf(resultArg.get(k));
                    }
                    two[i] = result;
                } else {
                    two[i] = new String[]{valueOf(arg)};
                }
            }
            return two;
        }
    }
	
	public static String valueOf(Object obj) {
        return (obj == null) ? null : obj.toString();
    }
	
	public static CallContractTransaction newCallTx(int chainId, int assetsId, BigInteger senderBalance, String nonce, CallContractData callContractData, String remark) {
        try {
            CallContractTransaction tx = new CallContractTransaction();
            if (StringUtils.isNotBlank(remark)) {
                tx.setRemark(remark.getBytes(StandardCharsets.UTF_8));
            }
            tx.setTime(System.currentTimeMillis() / 1000);
            // 计算CoinData
            CoinData coinData = makeCoinData(chainId, assetsId, senderBalance, nonce, callContractData, tx.size(), calcSize(callContractData));
            tx.setTxDataObj(callContractData);
            tx.setCoinDataObj(coinData);
            tx.serializeData();
            return tx;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
	
	private static CoinData makeCoinData(int chainId, int assetsId, BigInteger senderBalance, String nonce, ContractData contractData, int txSize, int txDataSize) {
        CoinData coinData = new CoinData();
        long gasUsed = contractData.getGasLimit();
        BigInteger imputedValue = BigInteger.valueOf(LongUtils.mul(gasUsed, contractData.getPrice()));
        // 总花费
        BigInteger value = contractData.getValue();
        BigInteger totalValue = imputedValue.add(value);

        CoinFrom coinFrom = new CoinFrom(contractData.getSender(), chainId, assetsId, totalValue, RPCUtil.decode(nonce), (byte) 0);
        coinData.addFrom(coinFrom);

        if (value.compareTo(BigInteger.ZERO) > 0) {
            CoinTo coinTo = new CoinTo(contractData.getContractAddress(), chainId, assetsId, value);
            coinData.addTo(coinTo);
        }

        BigInteger fee = TransactionFeeCalculator.getNormalUnsignedTxFee(txSize + txDataSize + calcSize(coinData));
        totalValue = totalValue.add(fee);
        if (senderBalance.compareTo(totalValue) < 0) {
            // Insufficient balance
            throw new RuntimeException("Insufficient balance");
        }
        coinFrom.setAmount(totalValue);
        return coinData;
    }
	
	private static int calcSize(NulsData nulsData) {
        if (nulsData == null) {
            return 0;
        }
        int size = nulsData.size();
        // 计算tx.size()时，当coinData和txData为空时，计算了1个长度，若此时nulsData不为空，则要扣减这1个长度
        return VarInt.sizeOf(size) + size - 1;
    }
	
	public static Result getSuccess() {
        return Result.getSuccess(CommonCodeConstanst.SUCCESS);
    }
	
	public static Result getFailed() {
        return Result.getFailed(CommonCodeConstanst.FAILED);
    }
	
	public static CreateContractTransaction newCreateTx(int chainId, int assetsId, BigInteger senderBalance, String nonce, CreateContractData createContractData, String remark) {
        try {
            CreateContractTransaction tx = new CreateContractTransaction();
            if (StringUtils.isNotBlank(remark)) {
                tx.setRemark(remark.getBytes(StandardCharsets.UTF_8));
            }
            tx.setTime(System.currentTimeMillis() / 1000);
            // 计算CoinData
            CoinData coinData = makeCoinData(chainId, assetsId, senderBalance, nonce, createContractData, tx.size(), calcSize(createContractData));
            tx.setTxDataObj(createContractData);
            tx.setCoinDataObj(coinData);
            tx.serializeData();
            return tx;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
	
	
}
