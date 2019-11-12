# C中常用的函数

## scanf 从控制台接收数据

使用方式样例
    
    #include <stdio.h>

    int main() {
        int integerNumber;
        scanf("%d", &integerNumber);  // 从控制台接收整数，赋值给integerNumber。注意，必须要加&符，具体含义指针章节会具体说明。
        printf("The integer number of you input is %d\n", integerNumber);
    
        float floatNumber;
        scanf("%f", &floatNumber);
        printf("The float number of you input is %f\n", floatNumber);
        return 0;
    }
        
输出结果如下：

    123  # 输入
    The integer number of you input is 123
    123.45  # 输入
    The float number of you input is 123.449997

> 输出结果浮点类型有误差，这个后面会解释。有兴趣可以去网上查一下。


## sizeof 计算变量占用内存大小

使用样例：

    #include <stdio.h>
    int main() {
        int num = 1;
        char c = 'c';
        char str[] = "hello,world!!!";
        printf("int: %d\n", sizeof num); // 也可以这样写
        printf("char: %d\n", sizeof(c));
        printf("char[]: %d\n", sizeof(str));
        return 0;
    }
    
    // 输出，单位字节
    int: 4
    char: 1
    char[]: 15
    

## strlen 计算字符串的长度

在C中，字符串是用char[]表示的，计算字符串长度，可以使用strlen。
不过需要引入“string.h”

例：

    #include <stdio.h>
    #include <string.h>
    int main() {
        char str[] = "hello,world";
        printf("The length of the str is %d", strlen(str));
        return 0;
    }
    
    // 输出：
    The length of the str is 11
    
## limits.h, float.h 包含了各种类型的边界值

这两个是头文件，里面包含了大量宏定义，基本都是基本数据类型的边界值：

例：

    #include <stdio.h>
    #include <limits.h>
    #include <float.h>
    
    int main(void) {
        printf("Some number limits for this system:\n");
        printf("Biggest int: %d\n", INT_MAX);
        printf("Smallest long long: %lld\n", LLONG_MIN);
        printf("One byte = %d bits on this system.\n", CHAR_BIT);
        printf("Largest double: %e\n", DBL_MAX);
        printf("Smallest normal float: %e\n", FLT_MIN);
        printf("float precision = %d digits\n", FLT_DIG);
        printf("float epsilon = %e\n", FLT_EPSILON);
        return 0;
    }

    // 输出：
    Some number limits for this system:
    Biggest int: 2147483647
    Smallest long long: -9223372036854775808
    One byte = 8 bits on this system.
    Largest double: 1.797693e+308
    Smallest normal float: 1.175494e-038
    float precision = 6 digits
    float epsilon = 1.192093e-007
    
