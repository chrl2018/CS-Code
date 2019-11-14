package com.example.redis_demo.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.Collections;
import java.util.List;

/**
 * @author chenruilong
 * @description
 * @ date 2019-11-12 9:55
 **/
public class RedisTool {

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;

    private static Logger logger = LoggerFactory.getLogger(RedisTool.class.getName());

    /**
     * 尝试获取分布式锁
     * @param jedis Redis客户端
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {
        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        if (LOCK_SUCCESS.equals(result)) {
            logger.info(Thread.currentThread().getName()+" -------- get lock success");
            return true;
        }
        return false;
    }

    /**
     * 释放分布式锁
     * @param jedis Redis客户端
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        if (RELEASE_SUCCESS.equals(result)) {
            logger.info(Thread.currentThread().getName()+" -------- release lock success");
            return true;
        }
        return false;
    }

    public static boolean getLock(String key,String value,int timeout,Jedis jedis) {
        try {
            if (jedis.setnx(key, value) == 1) {
                jedis.expire(key, timeout);
                logger.info(Thread.currentThread().getName()+" -------- get lock success");
                return true;
            }
            if (jedis.ttl(key) == -1) {
                jedis.expire(key, timeout);
            }
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean releaseLock(String key, String value,Jedis jedis) {
        try {
            while (true) {
                jedis.watch(key);
                if (value.equals(jedis.get(key))) {
                    Transaction transaction = jedis.multi();
                    transaction.del(key);
                    List<Object> list = transaction.exec();
                    if (list == null) {
                        continue;
                    }
                }
                jedis.unwatch();
                logger.info(Thread.currentThread().getName()+" -------- release lock success");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
