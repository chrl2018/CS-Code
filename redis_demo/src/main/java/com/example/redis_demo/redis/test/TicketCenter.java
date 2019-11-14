package com.example.redis_demo.redis.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

/**
 * @author chenruilong
 * @description  火车票管控中心
 * @ date 2019-11-13 17:35
 **/

public class TicketCenter {

    private static final java.lang.String redisServer = "localhost";

    //票据redis存储键值key
    private static final String REDIS_TICKET = "redis_ticket";

    private static final int redisPort = 6379;

    private static final int ticketCount = 1000;

    private static Logger logger = LoggerFactory.getLogger(TicketCenter.class.getName());

    public static void main(String[] args) {
        refreshTicket(ticketCount,redisServer,redisPort);
    }

    /**
     * 预制车票
     */
    public static void refreshTicket(int ticketCount,String redisServer,int redisPort){
        Jedis jedis = null;
        try{
            jedis = new Jedis(redisServer, redisPort);
            //重置车票
            if (StringUtils.isEmpty(jedis.get(REDIS_TICKET)) || Integer.valueOf(jedis.get(REDIS_TICKET)) <= 0){
                jedis.set(REDIS_TICKET,ticketCount+"");
                logger.info("refreshTicket success ticketKey:{},ticketCount:{}",REDIS_TICKET,ticketCount);
                return;
            }
            String currentCount = jedis.get(REDIS_TICKET);
            logger.info("refreshTicket failed,current ticketKey:{},ticketCount:{}",REDIS_TICKET,currentCount);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }

    }
}
