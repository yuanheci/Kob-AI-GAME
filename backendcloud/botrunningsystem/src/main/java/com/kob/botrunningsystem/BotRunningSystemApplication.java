package com.kob.botrunningsystem;

import com.kob.botrunningsystem.service.impl.BotRunningServiceImpl;
import com.kob.botrunningsystem.service.impl.utils.Bot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotRunningSystemApplication {
    public static void main(String[] args){
        BotRunningServiceImpl.botpool.start();   //启动线程
        SpringApplication.run(BotRunningSystemApplication.class, args);
    }
}
