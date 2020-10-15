package com.yzcm.common.cache.impl;
import javax.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.yzcm.common.cache.CacheService;
import com.yzcm.common.cache.util.RedisUtils;
@Service
public class CacheServiceImpl implements CacheService{
	 @Resource(name = "redisTemplate")
	 private RedisTemplate<String, Object> redisTemplate;
	 
	 public RedisUtils getRedisUtils(){
		 return new RedisUtils(redisTemplate);
	 }
	 /**
	  */
	 @SuppressWarnings("unchecked")
	 public <T> T cacheResult(String key) {
	     return (T) getRedisUtils().getDataFromCache(key);
	 }

	 /**
	 */
	 public void cacheRemove(String key) {
		 getRedisUtils().clearCache(key);
	 }
	 
	 /**
	  */
	 public <T> void cachePut(String key, T value) {
		 getRedisUtils().setDataToCache(key, value);
	 }
	 
	 /**
	  */
	 public <T> void cachePut(String key, T value,long express) {
		 getRedisUtils().setDataToCache(key, value,express);
	 }
	 
	 /**
	  */
	 public void cachePrexRemove(String prex) {
		 getRedisUtils().clearPrexCache(prex);
	 }
	 
	 /**
	  */
	 public void cacheRemoveOtherDB(String key,int dbindex) {
		 getRedisUtils().cacheRemoveOtherDB(key,dbindex);
	 }
}
