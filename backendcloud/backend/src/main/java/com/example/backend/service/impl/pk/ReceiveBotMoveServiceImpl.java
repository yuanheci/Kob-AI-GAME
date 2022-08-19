package com.example.backend.service.impl.pk;

import com.example.backend.consumer.WebSocketServer;
import com.example.backend.consumer.utils.Game;
import com.example.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
        System.out.println("receive bot move: " + userId + " " + direction + " ");

        //WebSocket的users是在下线的时候才从users中删除，保存的是链接
        if(WebSocketServer.users.get(userId) != null){
            //game中封装了一场游戏的当前全部信息
            Game game = WebSocketServer.users.get(userId).game;
            if(game != null){   //将AI计算结果返回game
                if(game.getPlayerA().getId().equals(userId)) {
                    game.setNextStepA(direction);
                }else if(game.getPlayerB().getId().equals(userId)){
                    game.setNextStepB(direction);
                }
            }
        }

        return "receive bot move success";
    }
}
