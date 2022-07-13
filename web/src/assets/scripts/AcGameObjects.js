const AC_GAME_OBJECTS = [];
//每一个前端的组件都对应一个 class ，由 class 来维护组件的功能， class 里面还可以细分成多个 class
//所有游戏组件的基类
export class AcGameObject {
    constructor() {
        //将前端所有游戏对象加入
        //注意这里的this是子类对象，后面的每一帧更新是对AC_GAME_OBJECTS中的元素的操作
        AC_GAME_OBJECTS.push(this);
        //上一帧执行的时刻与当前帧执行的时刻的时间间隔
        //时间间隔 * 物体移动速度
        this.timedelta = 0;
        this.has_called_start = false; //是否是第一帧
    }

    start() {  //只执行一次

    }

    update() {  //每一帧执行一次， 除了第一帧外

    }

    on_destroy() {  //删除之前执行

    }

    destroy() {
        this.on_destroy();

        for (let i in AC_GAME_OBJECTS) {
            const obj = AC_GAME_OBJECTS[i];
            if (obj === this) {
                AC_GAME_OBJECTS.splice(i); //删除数组中下标为i的元素
                break;
            }
        }
    }
}

let last_timestamp;  //上一次执行的时间
//用递归的方法实现前端所有游戏对象每秒都刷新60次
const step = timestamp => {
    for (let obj of AC_GAME_OBJECTS) {
        if (!obj.has_called_start) {
            obj.has_called_start = true;
            obj.start();
        } else {
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }
    last_timestamp = timestamp;
    requestAnimationFrame(step);
}
//动画制作，每秒刷新60帧
requestAnimationFrame(step)