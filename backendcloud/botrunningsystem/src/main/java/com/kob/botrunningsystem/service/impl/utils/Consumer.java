package com.kob.botrunningsystem.service.impl.utils;

import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class Consumer extends Thread{
    private Bot bot;
    private static RestTemplate restTemplate;
    private static final String receiveBotMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        Consumer.restTemplate = restTemplate;
    }

    public void startTimeout(long timeout, Bot bot){
        this.bot = bot;
        this.start();    //开启一个新线程

        //当新线程执行完了或等待时间达到timeout秒，就会结束等待
        //--用来控制评测代码的时间
        try {
            this.join(timeout);    //当前线程继续执行join
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.interrupt();   //中断当前线程
        }
    }

    private String addUid(String code, String uid){  //在code中的Bot类名后添加uid
        int k = code.indexOf(" implements java.util.function.Supplier<Integer>");
        return code.substring(0, k) + uid + code.substring(k);
    }

    @Override
    public void run() {   //编译代码
        UUID uuid = UUID.randomUUID();  //生成一个随机字符串
        String uid = uuid.toString().substring(0, 8);  //取前8位

        Supplier<Integer> botInterface = Reflect.compile(    //替换为java的函数接口Supplier
            "com.kob.botrunningsystem.utils.Bot" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();

        File file = new File("input.txt");
        try (PrintWriter fout = new PrintWriter(file)){
            fout.println(bot.getInput());
            fout.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Integer direction = botInterface.get();   //通过get返回一个direction数值
        System.out.println("move-direction: " + bot.getUserId() + " " + direction);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", direction.toString());

        restTemplate.postForObject(receiveBotMoveUrl, data, String.class);
    }
}
