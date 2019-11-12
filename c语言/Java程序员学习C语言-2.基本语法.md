Java程序员一般都具有一定的变成基础，像什么for，while，if这些都以比带过，或者可以不看

# 二、基本语法

## 2.1 Hello, world

    #include <stdio.h>  // c标准库
    
    int main() {
        printf("Hello, World!\n");   // System.out.printf();
        return 0;
    }    
    
## 2.2 基本数据类型

### 基本数据类型

- int
- short
- long
- char
- float
- double

> 注意，C语言中没有bool和String，0为false，1为true。String使用char[]。


## C语言中还提供了其他数据类型

- long long int: 可以简写为long long，这是C99标准中的。比long的存储空间还要大
- unsigned int: 可以简写为unsigned。无符号整型。该类型只能是整数。
- unsigned long int
- unsigned short int
- signed: 和unsigned。int就是signed int，不写的话，默认就是signed。


例：

    #include <stdio.h>
    
    int main() {
        unsigned int un = 3000000000;
        short end = 200;
        long big = 65537;
        long long verybig = 12345678908642;
        printf("un = %u and not %d\n", un, un);
        printf("end = %hd and %d\n", end, end);
        printf("big = %ld and not %hd\n", big, big);
        printf("verybig= %lld and not %ld\n", verybig, verybig);
        return 0;
    }
    
    输出：
    un = 3000000000 and not -1294967296
    end = 200 and 200
    big = 65537 and not 1
    verybig= 12345678908642 and not 1942899938



### 整型位数（占用内存）详解

C语言保证short一定小于“等于”int，long一定大于“等于”int。之所以有等于，是为了迎合不同类型的机器。

比如，在某些16位的机器上，int和short都是16位，long是32位。下面是不同机器各种占用情况：

Datetype  LP64   ILP64   LLP64   ILP32    LP32

char           8          8         8            8          8

short          16       16       16           16        16

int               32       64       32           32        16

long            64       64       32           32         32 

long long    64

pointer        64       64        64           32        32


不同的机器int占用的字节数不一样大，也就意味着范围不一样大。那这样可能就会影响程序。

为了解决这个问题，可以使用inttypes.h。这里面封装你需要的固定的位数的数据类型。

例如：

    #include <stdio.h>
    #include <inttypes.h>
    int main() {
        int32_t num = 32;  // 32位整型
        uint8_t num2 = 8;  // 8位无符号整型
        return 0;
    }        




## 2.3 printf输出控制符

### 控制符


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

printf函数有返回值，它的返回值就是它打印了多少字符：

    #include <stdio.h>
    
    int main(void) {
        int rv;
        rv = printf("hello,world!\n");
        printf("The printf() function printed %d characters.\n", rv);
        return 0;
    }
    
    //输出：
    hello,world!
    The printf() function printed 13 characters.


### 转移符号

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191112154503812.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poYW9ob25nZmVpXzM1OA==,size_16,color_FFFFFF,t_70)

例：

    #include <stdio.h>
    #include <windows.h>
    
    int main() {
        char fool[] = "You are fool.\b\b\b\b\bawesome!!!";
        for (int i = 0; i < 28; ++i) {
            Sleep(200);
            printf("%c", fool[i]);
        }
        return 0;
    }
    // 程序会先输出You are fool. 0.5秒之后，会删除fool.，然后输出awesome!!!
    // 该程序在ide中显示会有问题，在控制台效果会比较好。    


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

上述例子中，为 **3.14** 起了个别名**PI**，**home**起了个别名**NAME**，他们不是变量，没有数据类型，只是个别名。


还有一种常量定义方法， 就是加const关键字，这样变量就不可更改了：

    #include <stdio.h>
    #include <string.h>
    int main() {
        const int num = 3;
        num = 4;  // 这里会报错
        return 0;
    }


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

## 八进制和十六进制表示法

C语言中，定义八进制时，在前面加前缀0，如：

    int octalNum = 010;
    
定义十六进制时，在前缀加0x或0X，如：

    int hexNum = 0xA;
    int hexNum2 = 0Xb;
    
打印八进制时，使用%o，如：

    int octalNum = 010;
    printf("octal=%o", octalNum);
    
打印十六进制时，使用%x，如：

    int hexNum = 0xA;
    printf("hex=%x", hexNum);
    
打印带有前缀的八进制和十六进制，使用%#o和%#x，如：
    
    int octalNum = 010;
    int hexNum = 0xA;
    printf("octal=%#o, hex=%#x\n", octalNum, hexNum);
    
例子：

    #include <stdio.h>
    int main() {
        int hexNum = 0xA;
        int hexNum2 = 0Xb;
        int octalNum = 010;
        int decimalNum = 10;
        printf("hexNum: dec=%d, octal=%o, hex=%x\n", hexNum, hexNum, hexNum);
        printf("octalNum: dec=%d, octal=%o, hex=%x\n", octalNum, octalNum, octalNum);
        printf("decimalNum: dec=%d, octal=%o, hex=%x\n", decimalNum, decimalNum, decimalNum);
    
        printf("decimalNum with prefix: dec=%d, octal=%#o, hex=%#x\n", decimalNum, decimalNum, decimalNum);
        return 0;
    }

## typedef关键字

typedef可以将类型赋予别命，比如，将int赋予另一个别命myint

    #include <stdio.h>
    typedef int myint;  // 将int赋予另一个别命，myint
    int main(void) { 
        myint num = 3;   // 这样就可以使用myint定义变量，但其实还是int
        printf("%d", num);
        return 0;
    }
    
    // 输出
    3
    
> 这个和#define不同，#define可以将任何字符赋予别命，而typedef只能为类型赋予别命。

    