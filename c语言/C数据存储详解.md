# 数据存储详解

## bit、byte和word

- bit（位）：计算机的最小存储单元，一个bit就对应着一个“0或1”
- byte（字节）：byte通常被用作计算机的存储单元，意思就是计算机存储数据时按照byte为单位存储。1byte=8bit。8个bit连在一起就是1个byte。所以它可以表示0~255（0~2^8-1）
- word（字）：在计算机领域, 对于某种特定的计算机设计而言，字是用于表示其自然的数据单位的术语。在这个特定计算机中，字是其用来一次性处理事务的一个固定长度的位（bit）组。一个字的位数（即字长）是计算机系统结构中的一个重要特性。32位系统中，1word=32bit。64位系统中，1word=64bit。以此类推

## 整型在内存中的存储

整型（int、long等）在计算机中没有小数部分。一个整数是由一个或多个byte组成的。

比如，7在计算机存储时，最后是3个1。前面的都是0。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191111112332217.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poYW9ob25nZmVpXzM1OA==,size_16,color_FFFFFF,t_70)

最后一位是2^0，往前是2^1，以此类推。

## 浮点型在内存中的存储

浮点数与整数不同，它会被分割成两个部分：小数部分和指数部分。

比如：

    7.0 = 0.7 * 10^1  //小数部分为0.7，指数部分为1
    10.45 = 0.1045 * 10^2  // 小数部分为0.1045，指数部分为2
    
     
    
    