export class Cell {
    constructor(r, c) {
        this.r = r;
        this.c = c;
        //转换成canvas坐标系
        this.x = c + 0.5;
        this.y = r + 0.5;   //由于是画圆，所以要加上0.5格
    }
}