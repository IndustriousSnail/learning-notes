Java程序员一般都具有一定的变成基础，像什么for，while，if这些都以比带过，或者可以不看

# 二、基本语法

## 2.1 Hello, world

    #include <stdio.h>  // c标准库
    
    int main() {
        printf("Hello, World!\n");   // System.out.printf();
        return 0;
    }    
    
## 2.2 基本数据类型

- int
- short
- long
- char
- float
- double

> 注意，C语言中没有bool和String，0为false，1为true。String使用char[]。

## 2.3 printf输出控制符


|  控制符   | 说明  |
|  ----  | ----  |
|%d	| 按十进制整型数据的实际长度输出。|
|%ld	| 输出长整型数据。|
|%md	| m 为指定的输出字段的宽度。如果数据的位数小于 m，则左端补以空格，若大于 m，则按实际位数输出。|
|%u	| 输出无符号整型（unsigned）。输出无符号整型时也可以用 %d，这时是将无符号转换成有符号数，然后输出。但编程的时候最好不要这么写，因为这样要进行一次转换，使 CPU 多做一次无用功。|
|%c	| 用来输出一个字符。|
|%f	| 用来输出实数，包括单精度和双精度，以小数形式输出。不指定字段宽度，由系统自动指定，整数部分全部输出，小数部分输出 6 位，超过 6 位的四舍五入。|
|%.mf	| 输出实数时小数点后保留 m 位，注意 m 前面有个点。|
|%o	| 以八进制整数形式输出，这个就用得很少了，了解一下就行了。|
|%s	| 用来输出字符串。用 %s 输出字符串同前面直接输出字符串是一样的。但是此时要先定义字符数组或字符指针存储或指向字符串，这个稍后再讲。|


示例：

    #include <stdio.h>
    
    int main() {
        int num = 21;
        long longNum = 23;
        int negative = -22;
        char c = 'c';
        char * str = "string";  // c语言定义字符串
        float f = 1.2345678f;
        printf("%d, %ld, %1d, %u, %c, %f, %.2f, %o, %s", num, longNum, num, negative, c, f, f, num, str);
        return 0;
    }
    
    // 输出结果
    21, 23, 21, 4294967274, c, 1.234568, 1.23, 25, string
    
> 注意，C语言中不能像Java那样自动进行类型转换。比如 "a" + 32


## 2.4 宏定义（符号常量）

定义方式：

    #define name replacement list
    
    
示例：

    #include <stdio.h>

    #define PI 3.14
    #define NAME home
    
    int main() {
        float area = PI * 3 * 3;
        int NAME = 1;
        printf("The area of home%d is %.2f", home, area);
        return 0;
    }
    
    // 输出结果
    The area of home1 is 28.26
    
> 这与Java中的常量定义不同，C语言中，只是给某个东西赋予了另一个名字。

上述例子中，为 **3.14** 起了个别名**PI**，**home**起了个别名**NAME&**，他们不是变量，没有数据类型，只是个别名。


## 2.5 从控制台接收输入

    #include <stdio.h>
    
    int main() {
        int i = getchar();  // 类似java的 System.in.read()
        putchar(i);         // 类似java的 System.out.print();
        return 0;
    }
    

## 2.6 数组

    #include <stdio.h>
    
    int main() {
        int nums[3];  // 不初始化，取随机数
        for (int i = 0; i < 3; ++i) {
            printf("%d, ", nums[i]);
        }
    
        printf("\n");
        int numsInit[3] = {1,2,3};  // 定义数组并初始化
        for (int j = 0; j < 3; ++j) {
            printf("%d, ", numsInit[j]);
        }
    }
    
    // 输出结果
    0, 10556336, 0,  //随机数
    1, 2, 3,    
    
> C语言中，如果定义数组后不进行初始化，会取随机数。


## 2.7 定义函数

    #include <stdio.h>
    
    int sum(int a, int b);
    
    int main() {
        printf("%d", sum(1, 2));
        return 0;
    }
    
    int sum(int a, int b) {
        return a + b;
    }
    
    //输出结果
    3
    
> C语言与Java定义方法的区别在于，C语言的函数必须写在调用者的前面，如果要写在后面的话，需要在前面先定义。

## 2.8 定义全局变量

    #include <stdio.h>

    int count=0;
    
    void add() {
        count++;
    }
    
    int main() {
        count++;
        add();
        printf("%d", count);
        return 0;
    }
    
    // 输出结果
    2
        
    
## 2.8 条件、循环语句

- if、for、while、do while、switch这些和java中都一样，就不再说了


### Goto语句

    #include <stdio.h>

    int main() {
        int num = 0;
        label:
        printf("num: %d\n", num);
        num++;
        if (num < 2)
            goto label;
        return 0;
    }
    
Goto语句就是定义一个label，然后不管程序执行到哪，都可以使用goto语句跳到label行执行。


## 2.9 静态变量

    #include <stdio.h>
    
    void add() {
        static int count=0;  // 定义count为add函数的静态变量
        count++;
        printf("count: %d\n", count);
    }
    
    int main() {
        add();
        add();
        add();
    }
    
    // 输出结果
    count: 1
    count: 2
    count: 3
    
静态变量意味着变量只初始化一次，则再次调用函数时变量不会重新初始化，还是用上次的那个。    


# 三、进阶语法

## 3.1 头文件

