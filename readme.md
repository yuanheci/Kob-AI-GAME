## 一个伟大的史诗级巨著
### 简介
一款在线的程序对抗平台，AI 可以根据已有的游戏规则进行比赛一决胜负。同时支持人机，人人对战。

### 游戏规则

&emsp;&emsp;确切地说，这并不是贪吃蛇。 与传统单人贪吃蛇不同的是，本贪吃蛇为双人对战，每回合玩家同时做出决策控制自己的蛇。
玩家在`13*14`的网格中操纵一条蛇(蛇是一系列坐标构成的有限不重复有顺序的序列，序列中相邻坐标均相邻，即两坐标的x轴坐标或y轴坐标相同，序列中第一个坐标代表蛇头)，玩家只能控制蛇头的朝向(东、南、西和北)来控制蛇。蛇以恒定的速度前进(前进即为序列头插入蛇头指向方向下一格坐标，并删除序列末尾坐标)。蛇的初始位置在网格中的左下角(地图位置[13,1])与右上角(地图位置[1,14])，初始长度为1格。与传统贪吃蛇不同，本游戏在网格中并没有豆子，但蛇会自动长大(长大即为不删除序列末尾坐标的前进)，前10回合每回合长度增加1，从第11回合开始，每3回合长度增加1。

&emsp;&emsp;地图为`13*14`的网格，由`1*1`的草地与障碍物构成。

&emsp;&emsp;蛇头在网格外、障碍物、自己蛇的身体(即序列重复)、对方蛇的身体(即与对方序列有相同坐标)，或非法操作均判定为死亡。任何一条蛇死亡时，游戏结束。若蛇同时死亡，判定为平局，否则先死的一方输，另一方赢。

+ 人类操作：用键盘WASD控制己方蛇的移动。
+ AI操作：可以将自己写的代码上传到平台上来创建AI蛇，代码写法见一下模板：

**这里提供了一个可以自动判断上下左右是否有障碍物，选择合适方向的简易Bot，你可以基于本模板开发自己的Bot程序。**

**你只需要修改`nextMove`方法。**

```java
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

        for(int i = 0; i < steps.length(); i++){
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

    //获取地图上的障碍物   ---> 玩家需要修改核心逻辑代码！！
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

```
---

### 创建AI Bot方法：
![image](https://github.com/yuanheci/kob/assets/97277559/f1b09c0c-9d85-471a-87bf-0ac07e93daf3)

![image](https://github.com/yuanheci/kob/assets/97277559/b637d765-19c1-4ac1-a0b4-4d25adb956f3)

![image](https://github.com/yuanheci/kob/assets/97277559/b7a69763-48b6-451d-946a-86bae5940535)

---

## Demo
[体验地址](https://kob.yuanheci.top)

![333](https://github.com/yuanheci/kob/assets/97277559/07540a4c-5530-4d52-ae92-7cc0407a25c1)

![image](https://github.com/yuanheci/kob/assets/97277559/733c01bb-8071-4f5d-8b23-087d44dc7a7f)
