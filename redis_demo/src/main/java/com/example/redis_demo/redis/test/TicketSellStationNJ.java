package com.example.redis_demo.redis.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chenruilong
 * @description  模拟不同进程进行售票  可抽象理解为多地售票：南京售票点
 * @ date 2019-11-12 14:55
 **/

public class TicketSellStationNJ {
    //模拟多个窗口 进行售票
    private static final int concurrency = 5;

    private static ExecutorService executorService = Executors.newFixedThreadPool(concurrency);

    //车站名称
    private static final String stationName = "南京南站";

    private static Logger logger = LoggerFactory.getLogger(TicketSellStationSS.class.getName());

    public static void main(String[] args) {
        final CyclicBarrier barrier = new CyclicBarrier(concurrency);
        final CountDownLatch count = new CountDownLatch(concurrency);
        // 用于统计 执行时长
        StopWatch watch = new StopWatch();
        watch.start();
        TicketSell tickets = new TicketSell(count,barrier,stationName);
        for (int i = 0; i < concurrency; i++) {
            executorService.submit(tickets);
        }
        try {
            count.await();
            executorService.shutdown();
            tickets.printTicketBill(stationName);
            watch.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if (watch.isRunning()){
                watch.stop();
            }
            logger.info("南京南站总耗时:" + watch.getTotalTimeSeconds() + "秒");
        }
    }
}
