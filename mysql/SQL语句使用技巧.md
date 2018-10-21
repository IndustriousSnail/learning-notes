<h1>MYSQL中SQL语句使用技巧和优化查询总结</h1>

目录
* [一、SQL语言介绍](#1)
    * [1.1 sql语句的分类](#1.1)
    * [1.2 SQL语句的重要性](#1.2)
* [二、SQL开发技巧](#2)
    * [2.1 Join从句的使用](#2.1)
        * [2.1.1 SQL标准中Join的类型](#2.1.1)
        * [2.1.2 利用左连接(Left Join)求A,B表的差集](#2.1.2)
        * [2.1.3 使用全连接(Full Join)求A,B全集与交集的差](#2.1.3) 
        * [2.1.4 Mysql中使用全连接(Full Join)](#2.1.4)
        * [2.1.5 交叉连接（笛卡尔积连接）Cross Join](#2.1.5)
        * [2.1.6 利用join语句条件进行update操作](#2.1.6)
        

<h2 id="1">一、SQL语言介绍</h2>
<h3 id="1.1">1.1 sql语句的分类</h3>
分为以下四类：

1. DDL（Data Definition Language）数据定义语句：create、alter等，用于定义数据库中的对象。<br>
2. TPL（Transactional Process Language） 事务处理语句：启动事务、commit等<br>
3. DML（Data Manipulation Language）数据操作语句：增删改查等。<br>
4. DCL（Data Control Language）数据控制语句：控制数据库访问权限等语句。

<h3 id="1.2">1.2 SQL语句的重要性</h3>

1. 增加数据库处理效率，减少应用响应时间。<br>
2. 减少数据库服务器负载，增加服务器稳定性。<br>
3. 减少服务器间通讯的网络流量。

<h2 id="2">二、SQL开发技巧</h2>
<h3 id="2.1">2.1 Join从句的使用</h3>
<h4 id="2.1.1">2.1.1 SQL标准中Join的类型</h4>

1. 内连接（inner）
2. 全外连接（full outer）
3. 左外连接（left outer）
4. 右外连接（right outer）
5. 交叉连接（cross）

<h4 id="2.1.2">2.1.2 利用左连接(Left Join)求A,B表的差集</h4>
当A，B两表使用左连接进行关联时，求出的是A的全部内容和包含在A表中B表部分。对于不在B表中的A表内容，B的key以及其他查询内容会填充NULL。

    select <select_list> from TableA A left join TableB B on A.key = B.key
    
上面提到，当正常的左连接时：“不在B表中的A表内容，B的key以及其他查询内容会填充NULL。” 所以只需要增加where条件，只选择“B.key is null的部分”。
这样就可以求出“在A表中，但不在B表中的数据”，及“A,B表的差集”。即“A-B”

    select <select_list> from TableA A left join TableB B on A.key = B.key where B.key is NULL
    
<h4 id="2.1.3">2.1.3 使用全连接(Full Join)求A,B全集与交集的差</h4>
这里要求出的是“属于A，B中，但是不属于A∩B的内容”。也就是查询在A表B表中，去除A表B表共有的部分。
全连接可以查询出A表和B表的所有部分。如果在A表，不在B表，则和左连接一样，B的内容会填充NULL，同理A也一样。
即“A-(A∩B)”

    select <select_list> from TableA A full join TableB B on A.key = B.key where A.key is NULL or B.key is NULL
    
<h4 id="2.1.4">2.1.4 Mysql中使用全连接(Full Join)</h5>
在Mysql中，是不支持full join的。如果要使用，可以使用以下方法：<br><br>
1.使用union all和left join和right join实现。

    select <select_list> from TableA A left join TableB B on A.key = B.key
    union all
    select <select_list> from TableA A right join TableB B on A.key = B.key
    
<h4 id="2.1.5">2.1.5 交叉连接（笛卡尔积连接）Cross Join</h4>
交叉连接，又称为笛卡尔积连接或叉乘。如果A和B是两个集合，他们的交叉连接就记作A×B。

    select <select_list> from TableA A cross join TableB B
    
交叉连接中，不需要使用on从句。如果A表有 n 条记录，B表有 m 条记录，则总查询结果为 n*m 条记录。
A表中的每条记录会和B表中的每条记录进行关联。

<h4 id="2.1.6">2.1.6 利用join语句条件进行update操作</h4>
考虑如下情况，比如我们需要更新一条语句，但是where条件比较复杂，需要用语句实现，然后进行join关联。我们可以使用如下语句。

    update TableA A inner join (select key from TableB) B on a.key=b.key 
    set A.name='newData'
    
<h4 id="2.1.7">2.1.7 使用Join和Group By优化子查询</h4>
如下场景，A表中记录了id和名称，B表记录了id和数量。

    select <select_list> from TableA 
    where 
    

