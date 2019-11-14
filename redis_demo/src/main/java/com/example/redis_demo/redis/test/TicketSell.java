package com.example.redis_demo.redis.test;

import com.example.redis_demo.redis.RedisTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chenruilong
 * @description  售票具体实现类
 * @ date 2019-11-12 16:41
 **/

public class TicketSell extends Thread{

    private static Logger logger = LoggerFactory.getLogger(TicketSell.class.getName());

    private CountDownLatch count;

    private CyclicBarrier barrier;

    private static final Integer Lock_Timeout = 0;

    //redis锁key值
    private static final String lockKey = "LockKey";

    //票据redis存储键值key
    private static final String REDIS_TICKET = "redis_ticket";

    private static String stationName = null;

    private static AtomicInteger ticketNum = new AtomicInteger();

    private volatile static boolean  working  = true;
    //记录售出的票据
    private static Set<String> ticketBill = new HashSet<String>();

    public TicketSell(CountDownLatch count, CyclicBarrier barrier, String stationName) {
        this.stationName = stationName;
        this.count = count;
        this.barrier = barrier;
    }

    /**
     * 售票
     * @param jedis
     */
    public void sellTicket(Jedis jedis) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //以当前线程名称作为value
        String value = Thread.currentThread().getName();
        try{
            boolean getLock = RedisTool.getLock(lockKey,value,Lock_Timeout,jedis);
            String ticketValue = jedis.get(REDIS_TICKET);
            if(getLock && working && !StringUtils.isEmpty(ticketValue)){
                ticketNum.set(Integer.valueOf(ticketValue));
                if (ticketNum.get() >= 1) {
                    //待出售票据编码
                    String sellTicketCode = ticketNum.get()+"";
                    //票数减一
                    ticketNum.getAndDecrement();
                    //更新票据中心余票
                    jedis.set(REDIS_TICKET,ticketNum.get()+"");
                    //记录售票账单
                    ticketBill.add(sellTicketCode);
                    //打印售票账单
                    logger.info("================"+Thread.currentThread().getName()+"=================  "+stationName+"售票成功，票号:" + sellTicketCode+",还剩" + ticketNum.get() + "张票--" );
                }else {
                    logger.info(" ================ "+Thread.currentThread().getName()+"=================  "+stationName+"票已经售完!--");
                    working = false;
                }
            }else if(!working){
                logger.info(Thread.currentThread().getName()+" waiting to get the Lock ....");
                Thread.sleep(100);
            }
            Thread.sleep(1000);
            countDownLatch.countDown();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            countDownLatch.await();
            if(RedisTool.releaseLock(lockKey,value,jedis)){
                Thread.sleep(30);
            }
        }
    }

    public static void printTicketBill(String stationName){
        logger.info(stationName+" 售出车票总数：{},账单：{}",ticketBill.size(),ticketBill.toString());
    }

    public void run() {
        logger.info(Thread.currentThread().getName()+"到达,等待中...");
        Jedis jedis = new Jedis("localhost", 6379);
        try{
            //此处阻塞 等所有线程都到位后 一起进行抢票
            barrier.await();
            if(Thread.currentThread().getName().equals("pool-1-thread-1")){
                logger.info("-----------------全部线程准备就绪,开始抢票------------------");
            }else {
                Thread.sleep(100);
            }
            while (working) {
                sellTicket(jedis);
            }
            //当前线程结束后，计数器-1
            count.countDown();
            jedis.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
