package com.kob.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread{
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();  //条件变量
    private Queue<Bot> bots = new LinkedList<>();  //会在两个线程中被改变，一个是生产者线程，另一个是消费者线程。

    public void addBot(Integer userId, String botCode, String input){
        lock.lock();
        try{
            bots.add(new Bot(userId, botCode, input));
            condition.signalAll();    //此时队列非空了，唤醒阻塞中的线程
        }finally {
            lock.unlock();
        }
    }

    private void consume(Bot bot){
        Consumer consumer = new Consumer();
        consumer.startTimeout(2000, bot);
    }

    @Override
    public void run() {
        while(true){
            lock.lock();
            if(bots.isEmpty()){
                try {
                    //如果队列为空，就阻塞住该线程（会自动释放锁）
                    //等待被唤醒
                    condition.await();
                } catch (Exception e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            }else{
                Bot bot = bots.remove();  //remove(): 返回并删除队头
                lock.unlock();
                consume(bot);   //编译执行一段代码，比较耗时，可能会执行几秒钟，所以要放在解锁后面
            }
        }
    }
}
