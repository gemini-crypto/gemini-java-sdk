package com.yzcm.common.cache;
/**
 */
public interface CacheService {
	/**
     * @param key
     * @return
     */
    public <T> T cacheResult(String key);
    /**
     * @param key
     */
    public void cacheRemove(String key);
    /**
     * @param key
     */
    public void cachePrexRemove(String prex);
    /**
     * @param key
     * @param value
     */
    public <T> void cachePut(String key,T value);
    /**
     * @param key
     * @param value
     */
    public <T> void cachePut(String key,T value,long express);
    
    public void cacheRemoveOtherDB(String key,int dbindex);
    
}
