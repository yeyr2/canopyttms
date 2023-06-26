package com.ttms;

import com.ttms.dao.OrderDao;
import com.ttms.dao.TicketAndSeatDao;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Instant;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.ttms.mapper")
public class Start {
    public static void main(String[] args) {
        SpringApplication.run(Start.class);

        ScheduledThreadPoolExecutor exception = new ScheduledThreadPoolExecutor(2, (Runnable r) -> {
            Thread thread = new Thread(r);
            thread.setName("com.ttms.QuerySoleTicket");
            return thread;
        });

        TicketAndSeatDao ticketAndSeatDao = new TicketAndSeatDao();
        OrderDao orderDao = new OrderDao();

        // 启动sole轮询器
        exception.scheduleAtFixedRate(()->{
            System.out.println("检测未支付的票");
            Long time = Instant.now().toEpochMilli();
            try {
                ticketAndSeatDao.selectAndUpdateTicketByLock(time);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, 60*10,60*10, TimeUnit.SECONDS);

        exception.scheduleAtFixedRate(()->{
            System.out.println("检测过期的演出计划以及对应的票和id");
            try {
                orderDao.selectIsExpired();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, 30*10,60*10, TimeUnit.SECONDS);
    }
}
