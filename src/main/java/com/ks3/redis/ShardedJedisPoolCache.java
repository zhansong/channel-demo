package com.ks3.redis;

import com.ks3.exception.RedisErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.ks3.exception.ExceptionConstants.ErrorCode;

@Slf4j
@Component
@Primary
public class ShardedJedisPoolCache {

	private String storeServer = "127.0.0.1:6379";

	private String storePwd = "";

	private Integer cachePoolMaxActive = 200;

	private Integer cachePoolMaxIdle = 200;

	private Long cachePoolMaxWait = 1L;

	private Boolean cachePoolTestOnBorrow = false;

	private JedisPoolConfig jedisPoolConfig = null;

	private ShardedJedisPool shardedJedisPool = null;
	
	public void initJedisPoolConfig(){
		log.debug("init jedis config:{},{},{},{}",new Object[]{cachePoolMaxActive,cachePoolMaxIdle,cachePoolMaxWait,cachePoolTestOnBorrow} );
		jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(cachePoolMaxActive);
		jedisPoolConfig.setMaxIdle(cachePoolMaxIdle);
		jedisPoolConfig.setMaxWaitMillis(cachePoolMaxWait);
		jedisPoolConfig.setTestOnBorrow(cachePoolTestOnBorrow);
	}
	
	public ShardedJedisPoolCache() {
		log.debug("cacheServer = "+storeServer);
		initJedisPoolConfig();
		if(!StringUtils.isEmpty(storeServer)) {
			try{
				log.debug("cachePwd = "+storePwd);
				String[] hostPorts = storeServer.split(",");
				List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>();
				for(String portHost : hostPorts){
					String[] hp = portHost.split(":");
					JedisShardInfo jedisShardInfo = new JedisShardInfo(hp[0].trim(),Integer.parseInt(hp[1].trim()));
					if(!StringUtils.isEmpty(storePwd)) {
						jedisShardInfo.setPassword(storePwd);
					}
					jedisShardInfoList.add(jedisShardInfo);
				}
				shardedJedisPool = new ShardedJedisPool(jedisPoolConfig, jedisShardInfoList);
				log.info("cache 初始化成功");
			}catch(Exception e){
				log.error("cache 初始化失败", e);
			}
		}
	}

	
	public ShardedJedis getConn() {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
		} catch (Exception e) {
			log.error("获取缓存连接出现异常", e);
		}
		log.debug("threadId:{} get shardedJedis conn",Thread.currentThread().getId());
		return jedis;
	}

	
	public void closeConn(ShardedJedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	
	public void destroyPool() {
		shardedJedisPool.destroy();
	}
	
	
	public Boolean exists(String key) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.exists(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}

	public Boolean setIfAbsent(String key, String value){
		ShardedJedis j = null;
		try {
			j = getConn();
			long result = j.setnx(key, value);
			if (result == 1){
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}

	
	public String setex(String key, int seconds ,String value) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.setex(key, seconds, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Long hset(String key, String field, String value) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.hset(key, field, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}


	public String hget(String key, String field) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.hget(key, field);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Long hdel(String key, String... fields) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.hdel(key, fields);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Boolean hexists(String key, String field) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.hexists(key, field);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public String get(String key) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.get(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			closeConn(j);
		}
		return null;
	}

	
	public Long del(String key) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.del(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}

	
	public Long rpush(String key, String... values) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.rpush(key, values);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}

	
	public List<String> lrange(String key, long start, long end) {
		ShardedJedis j = null;
		try {
			j = getConn();
			List<String> list = j.lrange(key, start, end);
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Long llen(String key) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.llen(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public String lpop(String key) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.lpop(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Long lpush(String key, String... values) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.lpush(key, values);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	public Long lrem(String key,long count,String value){
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.lrem(key, count, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	public String set(String key, String value) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.set(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Long zadd(String key, double score,String member) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.zadd(key, score, member);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Long zcard(String key) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.zcard(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Double zscore(String key,String member) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.zscore(key, member);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	
	public Long zadd(String key, Map<String,Double> scoreMembers) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.zadd(key, scoreMembers);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Set<String> zrange(String key, long start, long end) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.zrange(key, start, end);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Set<Tuple> zrangeWithScores(String key, long start,long end) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.zrangeWithScores(key, start, end);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Set<String> zrevrangeByScore(String key, double max,double min) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.zrevrangeByScore(key, max, min);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Set<String> zrevrangeByScore(String key, String max,String min,int offset,int count) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.zrevrangeByScore(key, max, min, offset, count);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Long incr(String key){
		ShardedJedis j = null;
		long count;
		try {
			j = getConn();
			count = j.incr(key);
			return count;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	public Long incr(String key,int timeout){
		ShardedJedis j = null;
		long count;
		try {
			j = getConn();
			count = j.incr(key);
			j.expire(key, timeout);
			return count;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	public Long decr(String key){
		ShardedJedis j = null;
		long count;
		try {
			j = getConn();
			count = j.decr(key);
			return count;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Map<String,String> hgetAll(String key){
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.hgetAll(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	
	public String ltrim(String key, long start, long end) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.ltrim(key, start, end);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Double zincrby(String key,double score,String member){
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.zincrby(key, score,member);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Set<Tuple> zrevrangeWithScores(String key,long start,long end) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.zrevrangeWithScores(key, start, end);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Long zrem(String key,String... members) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.zrem(key, members);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Set<String> zrevrange(String key,long start,long end) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.zrevrange(key, start, end);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public String getStoreServer() {
		return storeServer;
	}

	public void setStoreServer(String storeServer) {
		this.storeServer = storeServer;
	}
	
	
	public Boolean sismember(String key,String value) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.sismember(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Long scard(String key) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.scard(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Set<String> smembers(String key) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.smembers(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Long sadd(String key ,String... value) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.sadd(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public ScanResult<String> sscan(String key ,String cursor) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.sscan(key, cursor);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Long expire(String key,int seconds) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.expire(key, seconds);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Long srem(String key,String... members) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.srem(key, members);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public String srandmember(String key){
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.srandmember(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}
	
	
	public Long zrank(String key,String member) {
		ShardedJedis j = null;
		try {
			j = getConn();
			return j.zrank(key, member);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RedisErrorException(ErrorCode.Redis_Error);
		} finally {
			closeConn(j);
		}
	}

	public String getStorePwd() {
		return storePwd;
	}

	public void setStorePwd(String storePwd) {
		this.storePwd = storePwd;
	}

	public Integer getCachePoolMaxActive() {
		return cachePoolMaxActive;
	}

	public void setCachePoolMaxActive(Integer cachePoolMaxActive) {
		this.cachePoolMaxActive = cachePoolMaxActive;
	}

	public Integer getCachePoolMaxIdle() {
		return cachePoolMaxIdle;
	}

	public void setCachePoolMaxIdle(Integer cachePoolMaxIdle) {
		this.cachePoolMaxIdle = cachePoolMaxIdle;
	}

	public Long getCachePoolMaxWait() {
		return cachePoolMaxWait;
	}

	public void setCachePoolMaxWait(Long cachePoolMaxWait) {
		this.cachePoolMaxWait = cachePoolMaxWait;
	}

	public Boolean getCachePoolTestOnBorrow() {
		return cachePoolTestOnBorrow;
	}

	public void setCachePoolTestOnBorrow(Boolean cachePoolTestOnBorrow) {
		this.cachePoolTestOnBorrow = cachePoolTestOnBorrow;
	}

	public JedisPoolConfig getJedisPoolConfig() {
		return jedisPoolConfig;
	}

	public void setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
		this.jedisPoolConfig = jedisPoolConfig;
	}

	public ShardedJedisPool getShardedJedisPool() {
		return shardedJedisPool;
	}

	public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
		this.shardedJedisPool = shardedJedisPool;
	}
	
	
}
