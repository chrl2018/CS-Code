package com.example.redis_demo.redis;

import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Set;

/**
 * @author chenruilong
 * @description
 * @ date 2019-11-12 10:55
 **/

public class RedissionUtil {

    private static Logger logger = LoggerFactory.getLogger(RedissionUtil.class);

    private static RedissionUtil redisUtils;

    private RedissionUtil() {
    }

    /**
     * 提供单例模式
     *
     * @return
     */
    public static RedissionUtil getInstance() {
        if (redisUtils == null)
            synchronized (RedissionUtil.class) {
                if (redisUtils == null)
                    redisUtils = new RedissionUtil();
            }
        return redisUtils;
    }

    /**
     * 使用config创建Redisson Redisson是用于连接Redis Server的基础类
     *
     * @param config
     * @return
     */
    public static RedissonClient getRedisson(Config config) {
        RedissonClient redisson = Redisson.create(config);
        logger.info("成功连接Redis Server");
        return redisson;
    }

    /**
     * 使用ip地址和端口创建Redisson
     * 单机版redis
     * @param ip
     * @param port
     * @return
     */
    public static RedissonClient getSingleRedisson(String ip, String port) {
        Config config = new Config();
        config.useSingleServer().setAddress(ip + ":" + port);
        RedissonClient redisson = Redisson.create(config);
        logger.info(Thread.currentThread().getName()+"成功连接 Redis Server" + "\t" + "连接" + ip + ":" + port + "服务器");
        return redisson;
    }

    /**
     * 主从模式
     * @param masterAddress  主机地址
     * @param slaveAddress  从机地址集合
     * @return
     */
    public static RedissonClient getMasterSlaveRedisson(String masterAddress,Set<URL> slaveAddress){
        Config config = new Config();
        config.useMasterSlaveServers().
                setMasterAddress(masterAddress)
                .setSlaveAddresses(slaveAddress);
        RedissonClient redissonClient = Redisson.create(config);
        logger.info("成功连接Redis Server" + "\t" + "连接master：{}" + masterAddress + ", slave{}" + slaveAddress.toString() + "服务器");
        return redissonClient;
    }

    /**
     * 哨兵模式
     * @param masterName  主节点名称
     * @param sentineAds  从节点地址列表
     * @return
     */
    public static RedissonClient getSentineRedisson(String masterName,String... sentineAds){
        Config config = new Config();
        config.useSentinelServers()
                .setMasterName(masterName)
                .addSentinelAddress(sentineAds);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    /**
     * 集群模式
     * @param scanIntervalTime  扫描间隔时间  // cluster state scan interval in milliseconds
     * @param nodeAddress  集群节点地址列表
     * @return
     */
    public static RedissonClient getClusterRedisson(int scanIntervalTime,String... nodeAddress){
        Config config = new Config();
        config.useClusterServers()
                .setScanInterval(scanIntervalTime)
                .addNodeAddress(nodeAddress);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    /**
     * 关闭Redisson客户端连接
     *
     * @param redisson
     */
    public static void closeRedisson(RedissonClient redisson) {
        redisson.shutdown();
        logger.info(Thread.currentThread().getName()+"成功关闭Redis Client连接");
    }

    /**
     * 获取字符串对象
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public static <T> RBucket<T> getRBucket(RedissonClient redisson, String objectName) {
        RBucket<T> bucket = redisson.getBucket(objectName);
        return bucket;
    }

    /**
     * 获取Map对象
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public static <K, V> RMap<K, V> getRMap(RedissonClient redisson, String objectName) {
        RMap<K, V> map = redisson.getMap(objectName);
        return map;
    }

    /**
     * 获取有序集合
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public static <V> RSortedSet<V> getRSortedSet(RedissonClient redisson,
                                           String objectName) {
        RSortedSet<V> sortedSet = redisson.getSortedSet(objectName);
        return sortedSet;
    }

    /**
     * 获取集合
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public static <V> RSet<V> getRSet(RedissonClient redisson, String objectName) {
        RSet<V> rSet = redisson.getSet(objectName);
        return rSet;
    }

    /**
     * 获取列表
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public static <V> RList<V> getRList(RedissonClient redisson, String objectName) {
        RList<V> rList = redisson.getList(objectName);
        return rList;
    }

    /**
     * 获取队列
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public static <V> RQueue<V> getRQueue(RedissonClient redisson, String objectName) {
        RQueue<V> rQueue = redisson.getQueue(objectName);
        return rQueue;
    }

    /**
     * 获取双端队列
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public static <V> RDeque<V> getRDeque(RedissonClient redisson, String objectName) {
        RDeque<V> rDeque = redisson.getDeque(objectName);
        return rDeque;
    }

    /**
     * 此方法不可用在Redisson 1.2 中 在1.2.2版本中 可用
     *
     * @param redisson
     * @param objectName
     * @return
     */
    /**
     * public <V> RBlockingQueue<V> getRBlockingQueue(RedissonClient
     * redisson,String objectName){ RBlockingQueue
     * rb=redisson.getBlockingQueue(objectName); return rb; }
     */

    /**
     * 获取锁
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public static RLock getRLock(RedissonClient redisson, String objectName) {
        RLock rLock = redisson.getLock(objectName);
        logger.info(Thread.currentThread().getName()+"获取锁成功==========lock:{}",rLock.toString());
        return rLock;
    }

    /**
     * 获取原子数
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public static RAtomicLong getRAtomicLong(RedissonClient redisson, String objectName) {
        RAtomicLong rAtomicLong = redisson.getAtomicLong(objectName);
        return rAtomicLong;
    }

    /**
     * 获取记数锁
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public static RCountDownLatch getRCountDownLatch(RedissonClient redisson,
                                              String objectName) {
        RCountDownLatch rCountDownLatch = redisson
                .getCountDownLatch(objectName);
        return rCountDownLatch;
    }

    /**
     * 获取消息的Topic
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public static <M> RTopic<M> getRTopic(RedissonClient redisson, String objectName) {
        RTopic<M> rTopic = redisson.getTopic(objectName);
        return rTopic;
    }

}
