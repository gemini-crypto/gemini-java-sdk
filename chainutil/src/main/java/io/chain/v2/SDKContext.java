package io.chain.v2;

import java.math.BigInteger;

public class SDKContext {

    public static String DEFAULT_ENCODING = "UTF-8";
    /**
     */
    public static int main_chain_id = 1;
    /**
     */
    public static int main_asset_id = 1;
    /**
     */
    public static int nuls_chain_id = 1;
    /**
     */
    public static int nuls_asset_id = 1;
    /**
     */
    public static BigInteger NULS_DEFAULT_NORMAL_TX_FEE_PRICE = new BigInteger("100000");
    /**
     */
    public static BigInteger NULS_DEFAULT_OTHER_TX_FEE_PRICE = new BigInteger("100000");
    /**
     */
    public static int STOP_AGENT_LOCK_TIME = 259200;
    /**
     */
    public static String wallet_url = "http://127.0.0.1:9898/";

}
