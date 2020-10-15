package com.yzcm.common.cache.util;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisUtils {
    private RedisTemplate<String, Object> redisTemplate;
   
    public RedisUtils(RedisTemplate<String, Object> redisTemplate){
    	this.redisTemplate=redisTemplate;
    }
    public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
     * @param key
     * @return
     */
    public Object getDataFromCache(String key){
        BoundValueOperations<String, Object> bound = redisTemplate.boundValueOps(key);
        return bound.get();
    }
    
    /**
     * @param key
     * @param data
     */
    public void setDataToCache(String key,Object data){
        BoundValueOperations<String,Object> ops = redisTemplate.boundValueOps(key);
        ops.set(data);
    }
    
    /**
     * @param key
     * @return
     */
    public void setDataToCache(String key,Object data,long express){
        BoundValueOperations<String, Object> ops = redisTemplate.boundValueOps(key);
        ops.set(data);
        ops.expire(express, TimeUnit.SECONDS);
    }
    
    /**
     * @param cacheKey
     */
    public void clearCache(String cacheKey){
    	redisTemplate.delete(cacheKey);
    }
    
    /**
     * @param cacheKey
     */
    public void clearPrexCache(String prex){
    	Set<String> keys=redisTemplate.keys(prex+"*");
    	redisTemplate.delete(keys);
    }
    
    /**
     * @param cacheKey
     * @return
     */
    public List<Object> getCacheList(String cacheKey){
        BoundListOperations<String, Object> bound = redisTemplate.boundListOps(cacheKey);
        long size = bound.size();
        return bound.range(0, size);
    }
    /**
     * @param cacheKey
     * @param dataList
     */
    public void updatCacheList(String cacheKey,List<Object> dataList){
    	redisTemplate.delete(cacheKey);
        BoundListOperations<String, Object> bound = redisTemplate.boundListOps(cacheKey);
        bound.rightPushAll(dataList.toArray());
    }
    /**
     * @param cacheKey
     * @return
     */
    public Map<String, Object> getCacheMap(String cacheKey){
        BoundHashOperations<String, String, Object> bound = redisTemplate.boundHashOps(cacheKey);
        return bound.entries();
    }
    /**
     * @param cacheKey
     * @param key
     * @return
     */
    public Object getDataFromCacheMap(String cacheKey,Object key){
        BoundHashOperations<String, Object, Object> bound = redisTemplate.boundHashOps(cacheKey);
        return bound.get(key);
    }
    
    /**
     * @param cacheKey
     * @param key
     * @param value
     */
    public void setDataFromCacheMap(String cacheKey,Object key,Object value){
        BoundHashOperations<String, Object, Object> bound = redisTemplate.boundHashOps(cacheKey);
        bound.put(key, value);
    }
    
    /**
     * 
     * @param key
     */
    public void delMore(String... key) {
		for (int i = 0; i < key.length; i++) {
			del(key[i]);
		}
	}
    
	public void del(String key) {
		final String keyf = key;    
        redisTemplate.execute(new RedisCallback<Long>() {    
        public Long doInRedis(RedisConnection connection) throws DataAccessException {    
            	return connection.del(keyf.getBytes());
           	}    
        });
	}
    
    /**
     * @param cacheKey
     */
	public void cacheRemoveOtherDB(String key,int index){
    	 JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory) redisTemplate.getConnectionFactory();
         jedisConnectionFactory.setDatabase(index);
         redisTemplate.setConnectionFactory(jedisConnectionFactory);
         redisTemplate.delete(key);
    }
    
}