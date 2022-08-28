package com.kob.botrunningsystem.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Bot的AI代码，使用时需要粘贴到网站中，此处是方便编辑，并且备用.

public class Bot implements java.util.function.Supplier<Integer>{
    static class Cell {
        public int x, y;
        public Cell(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    private boolean check_tail_increasing(int steps){  //检验当前回合，蛇的长度是否增加
        if(steps <= 10) return true;
        return steps % 3 == 1;
    }

    public List<Cell> getCells(int sx, int sy, String steps) {
        List<Cell> res = new ArrayList<>();   //蛇的身体
        steps = steps.substring(1, steps.length() - 1);
        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));

        for(int i = 0; i< steps.length(); i++){
            int d = steps.charAt(i) - '0';
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if(!check_tail_increasing(++step)){
                res.remove(0);   //删除蛇尾
            }
        }
        return res;
    }

    //获取地图上的障碍物
    public Integer nextMove(String input) {
        String[] strs = input.split("#");
        int[][] g = new int[13][14];
        for(int i = 0, k = 0; i < 13; i++){
            for(int j = 0; j < 14; j++, k++){
                if(strs[0].charAt(k) == '1'){
                    g[i][j] = 1;
                }
            }
        }

        int aSx = Integer.parseInt(strs[1]), aSy = Integer.parseInt(strs[2]);
        int bSx = Integer.parseInt(strs[4]), bSy = Integer.parseInt(strs[5]);
        List<Cell> aCells = getCells(aSx, aSy, strs[3]);
        List<Cell> bCells = getCells(bSx, bSy, strs[6]);

        //标注当前两条蛇的身体在地图 g 中的位置
        for(Cell c : aCells) g[c.x][c.y] = 1;
        for(Cell c : bCells) g[c.x][c.y] = 1;

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        for(int i = 0; i < 4; i++){
            //蛇头的新位置
            int x = aCells.get(aCells.size() - 1).x + dx[i];
            int y = aCells.get(aCells.size() - 1).y + dy[i];
            if(x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0){
                return i;  //表示这方向可以走
            }
        }
        return 0;   //表示没有方向能走，那么就默认向上走
    }

    @Override
    public Integer get() {
        File file = new File("input.txt");
        try {
            Scanner sc = new Scanner(file);
            return nextMove(sc.next());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
