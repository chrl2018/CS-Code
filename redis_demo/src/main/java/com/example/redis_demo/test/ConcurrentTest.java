package com.example.redis_demo.test;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chenruilong
 * @description
 * @ date 2019-11-14 8:42
 **/

public class ConcurrentTest {

    private static ExecutorService executorService = Executors.newFixedThreadPool(20);

    private static final int size = 1000;

    private static CountDownLatch countDownLatch = new CountDownLatch(size);

    public static void main(String[] args) throws InterruptedException{
        for (int i=0;i<size;i++){
            executorService.execute(new IncremCount());
            countDownLatch.countDown();
        }
        countDownLatch.await();
        executorService.shutdown();
    }
}
