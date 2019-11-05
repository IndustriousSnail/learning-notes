有很多程序是用C写的，比如Redis，遇到问题时，翻源码，看不懂。所以学习C语言还是很有必要的。

# 一、环境安装

## Windows

我推荐使用CLion，也是Jetbrains公司出品的，Java程序员用起来比较好上手。安装教程参考：

    https://www.jianshu.com/p/1aa989808e15
    
## Linux

直接使用gcc

    // 假设有如下文件hello.c
    #include <stdio.h>
    int main(){
      printf("hello world");
      return 0;
    }
    
    # 编译文件
    gcc hello.c
    # 之后会生成hello.out文件，该文件为可执行文件
    $ ./hello.out
    hello world  # 输出结果
    
