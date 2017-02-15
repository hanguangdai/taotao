package com.taotao.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Service
public class RedisService {

	@Autowired(required=false)
	private ShardedJedisPool sharedJedisPool;
	
	/**
	 * 执行Redis操作
	 * @param function
	 * @return
	 */
	public <T> T execute(Function<T, ShardedJedis> function){
		ShardedJedis jedis = null;
		try {
			//从连接池获取分片对象
			jedis = sharedJedisPool.getResource();
			return function.callback(jedis);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(jedis != null){
				// 关闭，检验连接是否失效，有效则放回连接池中，无效重置状态
				jedis.close();
			}
		}
		return null;
	}
	
	/**
	 * 执行set操作
	 * @param key
	 * @param value
	 * @return
	 */
	public String set(final String key,final String value){
		return this.execute(new Function<String, ShardedJedis>() {

			@Override
			public String callback(ShardedJedis e) {
				// TODO Auto-generated method stub
				return e.set(key, value);
			}
		});
	}
	
	/**
	 * 执行get操作
	 * @param key
	 * @return
	 */
	public String get(final String key){
		return this.execute(new Function<String, ShardedJedis>() {

			@Override
			public String callback(ShardedJedis e) {
				// TODO Auto-generated method stub
				return e.get(key);
			}
		});
	}
	
	/**
	 * 执行Del操作
	 * @param key
	 * @return
	 */
	public Long del(final String key){
		return this.execute(new Function<Long, ShardedJedis>() {
			@Override
			public Long callback(ShardedJedis e) {
				// TODO Auto-generated method stub
				return e.del(key);
			}
		});
	}
	
	/**
	 * 设置生存时间，单位为秒
	 * @param key
	 * @param seconds
	 * @return
	 */
	public Long expire(final String key,final int seconds){
		return this.execute(new Function<Long, ShardedJedis>() {

			@Override
			public Long callback(ShardedJedis e) {
				// TODO Auto-generated method stub
				return e.expire(key, seconds);
			}
		});
	}
	
	/**
	 * 执行set操作，并且设置生存时间
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public String expire(final String key,final String value,final int seconds){
		return this.execute(new Function<String, ShardedJedis>() {

			@Override
			public String callback(ShardedJedis e) {
				String str = e.set(key, value);
				e.expire(key, seconds);
				return str;
			}
		});
	}
	
}
