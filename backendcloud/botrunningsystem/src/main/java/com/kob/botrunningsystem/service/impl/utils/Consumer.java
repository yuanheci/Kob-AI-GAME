package com.kob.botrunningsystem.service.impl.utils;

import com.kob.botrunningsystem.utils.BotInterface;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

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
        int k = code.indexOf(" implements BotInterface");
        return code.substring(0, k) + uid + code.substring(k);
    }

    @Override
    public void run() {   //编译代码
        UUID uuid = UUID.randomUUID();  //生成一个随机字符串
        String uid = uuid.toString().substring(0, 8);  //取前8位

        BotInterface botInterface = Reflect.compile(
            "com.kob.botrunningsystem.utils.Bot" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();

        Integer direction = botInterface.nextMove(bot.getInput());
        System.out.println("move-direction: " + bot.getUserId() + " " + direction);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", direction.toString());

        restTemplate.postForObject(receiveBotMoveUrl, data, String.class);
    }
}
