package com.example.backend.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.backend.consumer.utils.Game;
import com.example.backend.consumer.utils.JwtAuthentication;
import com.example.backend.mapper.UserMapper;
import com.example.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾, token就是用户的token
public class WebSocketServer {
    //全局的所有链接，对所有实例可见。
    //开辟线程安全的map
    final private static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    //开辟线程安全的set
    final private static CopyOnWriteArraySet<User> matchpool = new CopyOnWriteArraySet<>();

    private User user;
    private Session session = null;  //这个session是WebSocket的一个包

    //WebSocketServer不是spring原有的单例模式，所以要自己定义成static
    private static UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper = userMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接时自动调用
        //在服务器端开一个线程，new一个实例，维护一个链接
        //每个链接使用session来维护的
        this.session = session;
        System.out.println("connected!");
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);

        if(this.user != null){
            users.put(userId, this);
        }else{
            this.session.close();
        }

        //System.out.println(users);
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("disconnected!");
        if(this.user != null){
            users.remove(this.user.getId());
            matchpool.remove(this.user);
        }
    }

    private void startMatching() {
        System.out.println("start matching");
        matchpool.add(this.user);

        while(matchpool.size() >= 2){
            Iterator<User> it = matchpool.iterator();
            User a = it.next(), b = it.next();
            matchpool.remove(a);
            matchpool.remove(b);

            Game game = new Game(13, 14, 20);
            game.createMap();

            //将匹配后的信息传给前端
            JSONObject respA = new JSONObject();
            respA.put("event", "start-matching");
            respA.put("opponent_username", b.getUsername());
            respA.put("opponent_photo", b.getPhoto());
            respA.put("gamemap", game.getG());
            users.get(a.getId()).sendMessage(respA.toJSONString());

            JSONObject respB = new JSONObject();
            respB.put("event", "start-matching");
            respB.put("opponent_username", a.getUsername());
            respB.put("opponent_photo", a.getPhoto());
            respB.put("gamemap", game.getG());
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }

    private void stopMatching() {
        System.out.println("stop matching");
        matchpool.remove(this.user);
    }

    @OnMessage
    public void onMessage(String message, Session session) {  //一般会把onMessage当做路由
        // 从Client接收消息时自动调用
        System.out.println("receive message!");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start-matching".equals(event)){
            startMatching();
        }
        else if("stop-matching".equals(event)){
            stopMatching();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message){
        synchronized (this.session){   //由于是异步，所以要加上锁
            try{
                this.session.getBasicRemote().sendText(message);
            }catch(IOException e){
                System.out.println(e);
            }
        }
    }
}