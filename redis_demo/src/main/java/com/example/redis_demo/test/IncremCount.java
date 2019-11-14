package com.example.redis_demo.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author chenruilong
 * @description
 * @ date 2019-11-14 8:47
 **/

public class IncremCount implements Runnable{

    private static AtomicInteger count = new AtomicInteger(0);

    private static Integer count1 = 0;

    private static Logger logger  = LoggerFactory.getLogger(IncremCount.class.getName());

    public void run() {
//        logger.info(Thread.currentThread().getName() + ":count:" + count.incrementAndGet());
//        logger.info(Thread.currentThread().getName() + ":count:" + ++count1);
        synchronized (this){
            logger.info(Thread.currentThread().getName() + ":count:" + ++count1);
        }
    }

}
