package com.example.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.example.backend.consumer.WebSocketServer;
import com.example.backend.pojo.Bot;
import com.example.backend.pojo.Record;
import com.example.backend.pojo.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.backend.consumer.WebSocketServer.restTemplate;

public class Game extends Thread{
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g;
    private final static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    //定义两名玩家
    private final Player playerA, playerB;
    //两名玩家的下一步操作
    private Integer nextStepA = null;
    private Integer nextStepB = null;

    private ReentrantLock lock = new ReentrantLock();
    private String status = "playing";  //playing -> finished
    private String loser = "";  //all: 平局, A: A输， B: B输了

    private final static String addBotUrl = "http://127.0.0.1:3002/bot/add/";

    public Game(
            Integer rows,
            Integer cols,
            Integer inner_walls_count,
            Integer idA,
            Bot botA,
            Integer idB,
            Bot botB
    ){
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];

        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        //如果是亲自操作那么根据botId = -1查出来的botA为空，否则表示是AI，那么可以获得其代码
        if(botA != null){
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if(botB != null){
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }

        //这里认为A是最下角的蛇，B是右上角的蛇
        playerA = new Player(idA, botIdA, botCodeA, rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, botIdB, botCodeB, 1, cols - 2, new ArrayList<>());
    }

    public Player getPlayerA(){
        return playerA;
    }

    public Player getPlayerB(){
        return playerB;
    }

    //会在外部的链接中调用（收到前端信息时）---不在新开辟的线程
    public void setNextStepA(Integer nextStepA){
        lock.lock();
        try{
            this.nextStepA = nextStepA;
        }finally {   //不管是否出现异常都要解锁
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB){
        lock.lock();
        try{
            this.nextStepB = nextStepB;
        }finally {
            lock.unlock();
        }
    }

    public int[][] getG(){
        return g;
    }

    //使用flood-fill算法判断连通性
    private boolean check_connectivity(int sx, int sy, int tx, int ty){
        if(sx == tx && sy == ty) return true;
        g[sx][sy] = 1;
        for(int i = 0; i < 4; i++){
            int x = sx + dx[i], y = sy + dy[i];
            if(x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0){
                if(check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }
        g[sx][sy] = 0;
        return false;
    }

    private boolean draw(){  //画地图

        for(int i = 0; i < this.rows; i++)
            for(int j = 0; j < this.cols; j++)
                g[i][j] = 0;

        //给四周加上墙
        for(int r = 0; r < this.rows; r++){
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for(int c = 0; c < this.cols; c++){
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        //创建随机障碍物
        Random random = new Random();
        for(int i = 0; i < this.inner_walls_count; i++) {
            for (int j = 0; j < 1000; j++) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);
                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1)
                    continue;
                //不能是左下角和右上角点
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)
                    continue;
                //对称位置的两个方格同时放上，所以10*2=20，一共有20个随机障碍物
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }
        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap(){
        for(int i = 0; i < 1000; i++){
            if(draw()) break;
        }
    }

    //将当前的局面编码成字符串返回
    //认为设定编码格式：地图#me.sx#me.sy#我的操作序列#you.sx#you.sy#对手的操作序列
    private String getInput(Player player){
        Player me, you;
        if(playerA.getId().equals(player.getId())){
            me = playerA;
            you = playerB;
        }else{
            me = playerB;
            you = playerA;
        }

        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepsString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString() + ")";
    }

    //判断是否要发送代码
    private void sendBotCode(Player player){
        //System.out.println(player.getBotId());
        if(player.getBotId().equals(-1)) return;  //亲自出马，不需要执行代码
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));

        //WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
        restTemplate.postForObject(addBotUrl, data, String.class);
    }

    private boolean nextStep(){   //等待两名玩家的下一步操作
        //前端设定的是每秒画5个格子，所以画一个格子的时间至少为200ms,如果在此期间输入了多个操作，前端只会保留最后一个
        //这会导致中间的某些操作被遗漏，所以后端要至少间隔200ms向前端发送信息。
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendBotCode(playerA);
        sendBotCode(playerB);

        //每一步双方可以输入操作的时间为5s，每5s来检测一下双方是否均输入完毕
        for(int i = 0; i < 50; i++){
            try {
                Thread.sleep(100);
                lock.lock();
                try{
                    if(nextStepA != null && nextStepB != null){
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                }finally {
                    lock.unlock();
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    private void sendMove(){  //向两个Client传递下一步信息
        lock.lock();
        try{
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            nextStepA = nextStepB = null;
            sendAllMessge(resp.toJSONString());
        }finally {
            lock.unlock();
        }

    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB){
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);  //蛇头
        if(g[cell.x][cell.y] == 1) return false;   //蛇头撞到墙了

        //蛇头是否撞到自己身体
        for(int i = 0; i < n - 1; i++) {
            if(cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y)
                return false;
        }

        //蛇头是否撞到对方身体
        for(int i = 0; i < n - 1; i++){
            if(cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y)
                return false;
        }
        return true;
    }

    private void judge(){  //判断两条蛇的下一步操作是否合法
        //取出两条蛇
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);

        if(!validA || !validB){
            status = "finished";
            if(!validA && !validB){
                loser = "all";
            }else if(!validA){
                loser = "A";
            }else if(!validB){
                loser = "B";
            }
        }

    }

    //根据双方的WebSocket链接返回全部信息
    private void sendAllMessge(String message){
        if(WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if(WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void updateUserRating(Player player, Integer rating){
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }

    private void saveToDatabase(){
        //更新玩家的天梯分
        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();

        if("A".equals(loser)){
            ratingA -= 2;
            ratingB += 5;
        }else if("B".equals(loser)){
            ratingA += 5;
            ratingB -= 2;
        }

        updateUserRating(playerA, ratingA);
        updateUserRating(playerB, ratingB);

        Record record = new Record(
            null,
            playerA.getId(),
            playerA.getSx(),
            playerA.getSy(),
            playerB.getId(),
            playerB.getSx(),
            playerB.getSy(),
            playerA.getStepsString(),
            playerB.getStepsString(),
            getMapString(),
            loser,
            new Date()
        );
         WebSocketServer.recordMapper.insert(record);
    }

    private void sendReuslt(){  //向两名玩家广播结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessge(resp.toJSONString());
    }

    //开启一个新线程的入口函数--run();
    @Override
    public void run() { //开启新线程后，新的线程跑在这里
        for(int i = 0; i < 1000; i++){  //1000步之内游戏肯定结束了, 3 * 13 * 14 < 1000
            if(nextStep()){   //是否获取了两条蛇的下一步操作
                judge();
                if(status.equals("playing")){
                    //输入合法，将两名玩家的输入广播给前端，实现同步
                    sendMove();
                }else{  //不合法，游戏已经结束
                    sendReuslt();
                    break;
                }
            }else{
                status = "finished";
                lock.lock();
                try{
                    if(nextStepA == null && nextStepB == null){
                        loser = "all";
                    }else if(nextStepA == null){
                        loser = "A";
                    }else{
                        loser = "B";
                    }
                }finally {
                    lock.unlock();
                }
                sendReuslt();
                break;
            }
        }
    }
}
