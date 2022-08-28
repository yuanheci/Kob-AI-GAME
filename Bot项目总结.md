### 注意点

### 1、vue3 脚手架生成后报错 

+ should always be multi-word vue/multi-word-component-names

    附带脚手架生成文件结构目录解答ctrl+p打开vue.config.js

    加上：

    ![image-20220707234230313](../AppData/Roaming/Typora/typora-user-images/image-20220707234230313.png)

    ```javascript
    lintOnSave:false//关闭语法检查
    ```

---




### 2、关于AC_GAME_OBJECTS数组作用以及父类中this的解释：


![image-20220713122800085](../AppData/Roaming/Typora/typora-user-images/image-20220713122800085.png)

+ 1.如果父类只有有参的构造方法，
    那么子类的构造方法必须调用父类的构造方法。

+ 2.创建子类对象时，
    首先会调用父类的构造方法，然后在调用子类的构造方法。调用父类的构造方法，并不是创建父类对象，而是借用父类的构造方法，创建子类对象。


+ 3.由于子类继承了父类，内存中不但会划分子类的内存空间，也会给子类
    继承的所有父类的成员变量也会划分内存空间。所以父类中的this关键字指的不是父类对象，而是指的的子类对象，而在父类或子类中我们通过super关键字，来调用父类中成员。

+ 4.覆盖不会删除父类中的方法，而是对子类的实例隐藏，暂时不使用，
    而super可以调用这些隐藏的方法。
    
+ 5.this和super关键字
    			1.this关键字代表的子类对象，父类中this关键字和子类中this关键字，指的都是子类对象。
        			2.子类继承父类，会先调用父类的构造方法，然后调用子类的构造方法。
        			  它并不是创建父类对象，而是调用了父类构造方法创建了子类对象。
      所以super关键字不是对父类对象的引用，而是在子类或父类中，可以调用父类中的成员。	
      
      

---

#### javascript分类原数据类型和引用数据类型

+ 引用数据类型有对象Object, 数组[ ], 函数Function

+ 当传递的是引用数据类型时，改变后原数据也会改变！！！

所以对于二维数组g，y总用了json来做转换，这样得到的就是一个完全的复制品copy_g，其本身g不受影响

```javascript
const copy_g = JSON.parse(JSON.stringify(g));
```



---



**export 和 export default**

 		一个模块就是一个独立的文件。该文件内部的所有变量，外部无法获取。如果你希望外部能够读取模块内部的某个变量，就必须使用`export`关键字输出该变量。

​		export default 的 import 方式不需要使用大括号包裹。因为对于 export default 其输出的本来就只有一个接口，提供的是模块的默认接口，自然不需要使用大括号包裹。

**切记，一个js文件中，只能有一个export default； （使用exprot default时不能加花括号）**

**但是，一个js文件中，可以有多个export。**

```javascript
let main = () => {
    let $div = $('div');
    $div.on('click', () => {
        console.log('on click');

        $div.off('click');
    })
}

export default main    //不能加花括号
```



docker中关闭和启动Nginx

```shell
sudo /etc/init.d/nginx stop
sudo /etc/init.d/nginx start
```



