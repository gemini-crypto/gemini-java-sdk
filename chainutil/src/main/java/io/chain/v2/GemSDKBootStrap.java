package io.chain.v2;

import io.chain.v2.parse.I18nUtils;

public class GemSDKBootStrap {

    private static final String LANGUAGE = "en";
    private static final String LANGUAGE_PATH = "languages";


    /**
     */
    public static void init(int chainId, String httpUrl) {
        initChainId(chainId);
        if (httpUrl != null && !httpUrl.endsWith("/")) {
            httpUrl += "/";
        }
        SDKContext.wallet_url = httpUrl;
    }

    /**
     */
    public static void initMain(String httpUrl) {
        initChainId(1);
        if (httpUrl != null && !httpUrl.endsWith("/")) {
            httpUrl += "/";
        }
        SDKContext.wallet_url = httpUrl;
    }

    /**
     */
    public static void initTest(String httpUrl) {
        initChainId(2);
        if (httpUrl != null && !httpUrl.endsWith("/")) {
            httpUrl += "/";
        }
        SDKContext.wallet_url = httpUrl;
    }

    /**
     * sdk init
     */
    private static void initChainId(int chainId) {
        if (chainId < 1 || chainId > 65535) {
            throw new RuntimeException("[defaultChainId] is invalid");
        }
        SDKContext.main_chain_id = chainId;
        SDKContext.nuls_chain_id = chainId;
        I18nUtils.loadLanguage(GemSDKBootStrap.class, LANGUAGE_PATH, LANGUAGE);
    }


}
