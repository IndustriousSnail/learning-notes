## 一、什么是RDB

RDB（Redis Database）是redis持久化数据的一种方式。

## 二、生成RDB文件

### 2.1 文件生成策略

Redis首先会生成一个rdb临时文件，当持久化完成之后，将临时文件覆盖之前的rdb文件。
Redis会默认在redis的根目录生产“dump.rdb”文件。

### 2.2 save命令

    
    127.0.0.1:6379> save
    OK
    
该命令为同步命令，即同步时会阻塞其他命令    

### 2.3 bgsave

    127.0.0.1:6379> bgsave
    Background saving started
    
bgsave命令会在后台生成一个子进程来进行数据持久化。

1. 用户执行bgsave命令
2. redis使用fork()函数生成子进程
3. 返回客户端“Background saving started”
4. 子进程在后台创建RDB文件

> 第二步fork创建子进程的过程是同步执行的，一般情况下，这一步是非常快的，也有少数情况，这一步比较慢，如果比较慢，就会阻塞主进程

注意：
> 当执行bgsave时，会生成当前redis的内存快照，要保证有足够的内存。

### 2.4 save与bgsave对比

|命令|save |bgsave |
|--|--|--|
|IO类型|同步|异步|
|阻塞|是|是(执行fork时阻塞)|
|时间复杂度|O(n)|O(n)|
|优点|不消耗额外内存|不阻塞命令端命令|
|缺点|阻塞客户端命令|需要fork，消耗内存|



## 三、自动生成rdb文件

在Redis中，有如下配置：

    save=900 1          # 当900秒内发生1条及以上数据变化，则会自动执行bgsave命令
    save=300 10         # 当300秒内发生10条及以上数据变化，则会自动执行bgsave命令
    save=60 10000       # 当60秒内发生10000条及以上数据变化，则会自动执行bgsave命令
    ...                 # 该配置可以配多个


### 四、rdb相关配置

    # 生成的rdb文件的名称，默认为dump.rdb
    dbfilename=dump.rdb
    
    # rdb文件存放的位置，默认是当前目录
    dir=./
    
    # 当bgsave发生错误时，是否停止写入
    stop-writes-on-bgsave-error=yes
    
    # rdb文件是否采用压缩格式
    rdbcompression=yes
    
    # 是否对rdb文件进行校验和的检测
    rdbchecksum=yes
    
    
### 五、其他触发RDB文件生成的条件

#### 1. 全量复制

当进行主从全量复制时，会生成rdb文件进行全量复制。

#### 2. debug reload

当执行该命令时，会生成rdb文件

>该命令会save当前的rdb文件，并清空当前数据库，重新加载rdb，加载与启动时加载类似，加载过程中只能服务部分只读请求（比如info、ping等）

#### 3. shutdown

当关闭时，会生成rdb文件


### 六、RDB存在的问题

#### 6.1 耗时、耗性能

1. 每次拷贝都是全量拷贝，时间复杂度为O(n)，消耗时间，CPU、IO。
2. bgsave会fork出子进程，并且会对当前redis使用copy-on-write策略进行内存拷贝，比较消耗内存。尤其是在写量非常大的情况下。

#### 6.2 不可控、丢失数据

RDB相当于备份当前时间的数据。那样的话，如果redis宕机，那么在上次开始备份到宕机这个时间段的数据就会丢失。









