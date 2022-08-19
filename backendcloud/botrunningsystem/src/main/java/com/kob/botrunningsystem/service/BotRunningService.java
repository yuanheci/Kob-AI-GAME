package com.kob.botrunningsystem.service;

public interface BotRunningService {
    String addBot(Integer userId, String botCode, String input); //input--地图信息和蛇的身体信息
}
