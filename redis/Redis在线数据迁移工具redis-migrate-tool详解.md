# redis-migrate-tool

## 简介

redis-migrate-tool 是一个很方便并且很有用的工具，它用于在redis之间进行数据迁移。

## git地址

    https://github.com/vipshop/redis-migrate-tool
    
## 特点

- 快速。
- 多线程。
- 基于redis复制。
- 实时迁移。
- 迁移过程中，源集群不影响对外提供服务。
- 异构迁移。
- 支持Twemproxy集群，redis cluster集群，rdb文件 和 aof文件。
- 过滤功能。
- 当目标集群是Twemproxy，数据会跳过Twemproxy直接导入到后端的redis。
- 迁移状态显示。
- 完善的数据抽样校验。

# redis-migrate-tool安装

    $ git clone https://github.com/vipshop/redis-migrate-tool.git
    $ cd redis-migrate-tool
    $ yum -y install automake libtool autoconf bzip
    $ autoreconf -fvi
    $ ./configure
    $ make
    $ ./src/redis-migrate-tool
    
如果能正常输出内容，则说明安装成功


# 使用redis-migrate-tool进行迁移

假设现在又如下两个集群：

    # 拥有一个key。 700key1: 700value1
    127.0.0.1:7001 master 
    127.0.0.1:7002 master 
    127.0.0.1:7003 master 
    127.0.0.1:7004 slave  
    127.0.0.1:7005 slave  
    127.0.0.1:7006 slave  
    
    # 拥有一个key。 800key1: 800value1
    127.0.0.1:8001 master
    127.0.0.1:8002 master
    127.0.0.1:8003 master
    127.0.0.1:8004 slave
    127.0.0.1:8005 slave
    127.0.0.1:8006 slave
    
    
> 可以使用 [create-local-redis-cluster-easily](https://github.com/IndustriousSnail/create-local-redis-cluster-easily) 该项目在本地虚拟机快速搭建一个redis集群。
    

现在要将7001-7006集群的数据迁移到8001-8006集群。

首先，修改 rmt.conf 文件为：

    [source]
    type: redis cluster  # 源redis类型
    servers :
      - 127.0.0.1:7001   # 源redis地址，可以只填一个，保证cluster nodes能获取到正常信息即可
    
    [target]
    type: redis cluster  # 目标redis类型
    servers:
      - 127.0.0.1:8001   # 目标redis地址
    
    [common]
    listen: 0.0.0.0:8888  # 服务启动后，监听的地址和端口
    
启动迁移工具：

    ./src/redis-migrate-tool -c rmt.conf

输出这些表示迁移成功：

    [root@localhost redis-migrate-tool]# ./src/redis-migrate-tool -c rmt.conf 
    [2019-10-18 05:05:32.523] rmt_core.c:525 Nodes count of source group : 3
    [2019-10-18 05:05:32.523] rmt_core.c:526 Total threads count : 2
    [2019-10-18 05:05:32.523] rmt_core.c:527 Read threads count assigned: 1
    [2019-10-18 05:05:32.523] rmt_core.c:528 Write threads count assigned: 1
    [2019-10-18 05:05:32.524] rmt_core.c:836 instances_by_host:
    [2019-10-18 05:05:32.524] rmt_core.c:840 127.0.0.1:7002@17002
    [2019-10-18 05:05:32.524] rmt_core.c:840 127.0.0.1:7001@17001
    [2019-10-18 05:05:32.524] rmt_core.c:840 127.0.0.1:7003@17003
    [2019-10-18 05:05:32.524] rmt_core.c:842 
    [2019-10-18 05:05:32.524] rmt_core.c:2444 Total threads count in fact: 2
    [2019-10-18 05:05:32.524] rmt_core.c:2445 Read threads count in fact: 1
    [2019-10-18 05:05:32.524] rmt_core.c:2446 Write threads count in fact: 1
    [2019-10-18 05:05:32.524] rmt_core.c:2455 read thread(0):
    [2019-10-18 05:05:32.524] rmt_core.c:2461 127.0.0.1:7002@17002
    [2019-10-18 05:05:32.524] rmt_core.c:2461 127.0.0.1:7001@17001
    [2019-10-18 05:05:32.524] rmt_core.c:2461 127.0.0.1:7003@17003
    [2019-10-18 05:05:32.524] rmt_core.c:2488 write thread(0):
    [2019-10-18 05:05:32.524] rmt_core.c:2494 127.0.0.1:7002@17002
    [2019-10-18 05:05:32.524] rmt_core.c:2494 127.0.0.1:7001@17001
    [2019-10-18 05:05:32.524] rmt_core.c:2494 127.0.0.1:7003@17003
    [2019-10-18 05:05:32.524] rmt_core.c:2551 migrate job is running...
    [2019-10-18 05:05:32.525] rmt_redis.c:1706 Start connecting to MASTER[127.0.0.1:7002@17002].
    [2019-10-18 05:05:32.525] rmt_redis.c:1740 Master[127.0.0.1:7002@17002] replied to PING, replication can continue...
    [2019-10-18 05:05:32.525] rmt_redis.c:1051 Partial resynchronization for MASTER[127.0.0.1:7002@17002] not possible (no cached master).
    [2019-10-18 05:05:32.526] rmt_redis.c:1110 Full resync from MASTER[127.0.0.1:7002@17002]: f00d5c7260ae3652fe82b8c76b57b25e2ada8ed5:11300
    [2019-10-18 05:05:32.593] rmt_redis.c:1517 MASTER <-> SLAVE sync: receiving 200 bytes from master[127.0.0.1:7002@17002]
    [2019-10-18 05:05:32.593] rmt_redis.c:1623 MASTER <-> SLAVE sync: RDB data for node[127.0.0.1:7002@17002] is received, used: 0 s
    [2019-10-18 05:05:32.593] rmt_redis.c:1643 rdb file node127.0.0.1:7002@17002-1571346332526984-122413.rdb write complete
    [2019-10-18 05:05:32.594] rmt_redis.c:6601 Rdb file for node[127.0.0.1:7002@17002] parsed finished, use: 0 s.
    [2019-10-18 05:05:32.594] rmt_redis.c:1706 Start connecting to MASTER[127.0.0.1:7001@17001].
    [2019-10-18 05:05:32.595] rmt_redis.c:1740 Master[127.0.0.1:7001@17001] replied to PING, replication can continue...
    [2019-10-18 05:05:32.595] rmt_redis.c:1051 Partial resynchronization for MASTER[127.0.0.1:7001@17001] not possible (no cached master).
    [2019-10-18 05:05:32.596] rmt_redis.c:1110 Full resync from MASTER[127.0.0.1:7001@17001]: 56b1e3002b0382acfb526ad6bab81576526b2d62:11523
    [2019-10-18 05:05:32.693] rmt_redis.c:1517 MASTER <-> SLAVE sync: receiving 238 bytes from master[127.0.0.1:7001@17001]
    [2019-10-18 05:05:32.693] rmt_redis.c:1623 MASTER <-> SLAVE sync: RDB data for node[127.0.0.1:7001@17001] is received, used: 0 s
    [2019-10-18 05:05:32.693] rmt_redis.c:1643 rdb file node127.0.0.1:7001@17001-1571346332596103-122413.rdb write complete
    [2019-10-18 05:05:32.694] rmt_redis.c:6601 Rdb file for node[127.0.0.1:7001@17001] parsed finished, use: 0 s.
    [2019-10-18 05:05:32.695] rmt_redis.c:1706 Start connecting to MASTER[127.0.0.1:7003@17003].
    [2019-10-18 05:05:32.696] rmt_redis.c:1740 Master[127.0.0.1:7003@17003] replied to PING, replication can continue...
    [2019-10-18 05:05:32.696] rmt_redis.c:1051 Partial resynchronization for MASTER[127.0.0.1:7003@17003] not possible (no cached master).
    [2019-10-18 05:05:32.698] rmt_redis.c:1110 Full resync from MASTER[127.0.0.1:7003@17003]: 5ab5f6c86143bcca51d11d55158398e52e6ff8aa:11318
    [2019-10-18 05:05:32.794] rmt_redis.c:1517 MASTER <-> SLAVE sync: receiving 219 bytes from master[127.0.0.1:7003@17003]
    [2019-10-18 05:05:32.794] rmt_redis.c:1623 MASTER <-> SLAVE sync: RDB data for node[127.0.0.1:7003@17003] is received, used: 0 s
    [2019-10-18 05:05:32.794] rmt_redis.c:1643 rdb file node127.0.0.1:7003@17003-1571346332698559-122413.rdb write complete
    [2019-10-18 05:05:32.795] rmt_redis.c:6601 Rdb file for node[127.0.0.1:7003@17003] parsed finished, use: 0 s.
    [2019-10-18 05:05:32.795] rmt_redis.c:6709 All nodes' rdb file parsed finished for this write thread(0).

> 此时对7001集群的操作会实时同步到8001集群，实现集群之间的数据同步


# rmt_redis.c:6446 ERROR: Can't handle RDB format version 错误解决方案

参考这篇文章：

    https://blog.csdn.net/zhaohongfei_358/article/details/102665418


# redis-migrate-tool 命令详解

    [root@localhost redis-migrate-tool]# ./src/redis-migrate-tool -h
    This is redis-migrate-tool-0.1.0
    
    Usage: redis-migrate-tool [-?hVdIn] [-v verbosity level] [-o output file]
                      [-c conf file] [-C command]
                      [-f source address] [-t target address]
                      [-p pid file] [-m mbuf size] [-r target role]
                      [-T thread number] [-b buffer size]
    
    Options:
      -h, --help             : this help  帮助
      -V, --version          : show version and exit 打印版本
      -d, --daemonize        : run as a daemon  以守护进程的方式运行
      -I, --information      : print some useful information  打印一些使用信息
      -n, --noreply          : don't receive the target redis reply  不接收目标集群回复
      -v, --verbosity=N      : set logging level (default: 5, min: 0, max: 11)  设置日志级别（默认：5，最小：0，最大11）
      -o, --output=S         : set logging file (default: stderr)  设置日志文件（默认：stderr）
      -c, --conf-file=S      : set configuration file (default: rmt.conf)  设置配置文件（默认：rmt.conf）
      -p, --pid-file=S       : set pid file (default: off)  设置pid文件（默认：off）
      -m, --mbuf-size=N      : set mbuf size (default: 512) 设置mbuf的大小（默认：512）
      -C, --command=S        : set command to execute (default: redis_migrate) 设置要执行的命令（默认：redis_migrate）
      -r, --source-role=S    : set the source role (default: single, you can input: single, twemproxy or redis_cluster) 设置源redis的类型（默认：single，你可以输入：single, twemproxy 或 redis_cluster）
      -R, --target-role=S    : set the target role (default: single, you can input: single, twemproxy or redis_cluster) 设置目标redis的类型（默认：single，你可以输入：single, twemproxy 或 redis_cluster）
      -T, --thread=N         : set how many threads to run the job(default: 1) 设置运行该程序的线程数量（默认：1） 
      -b, --buffer=S         : set buffer size to run the job (default: 1048576 byte, unit:G/M/K) 设置运行该程序的缓冲区大小（默认：1048576，单位：G/M/K）
      -f, --from=S           : set source redis address (default: 127.0.0.1:6379) 设置源redis地址（默认：127.0.0.1:6379）
      -t, --to=S             : set target redis group address (default: 127.0.0.1:6380) 设置目标redis地址（默认：127.0.0.1:6380）
      -s, --step=N           : set step (default: 1) 设置step级别（默认：1）
    
    Commands:
        redis_migrate        : Migrate data from source group to target group.  
                               将数据从源redis迁移到目标redis 
        redis_check          : Compare data between source group and target group. Default compare 1000 keys. You can set a key count behind.
                               比较源redis和目标redis的数据。默认比较1000个key。你可以在后面设置key的数量
        redis_testinsert     : Just for test! Insert some string, list, set, zset and hash keys into the source redis group. Default 1000 keys. You can set key type and key count behind.
                               只是测试用。插入一些string, list, set, zset 和 hash 类型的key到源redis中。默认1000个key，你也可以在后面指定key类型和key的数量。
                               
> 选项配置的具体含义下面的rmt.conf配置文件中有详细说明

# rmt.conf详解

配置文件分为三个部分：source、target和common。

## source和target

- type：表示这组redis的类型。可以是这些值：single、twemproxy、redis cluster、rdb file、aof file。
- server: 这组的redis地址列表。 如果类型是twemproxy（Redis的代理），那么就和twemproxy配置文件差不多。如果类型是rdb文件，那么这个就是文件全路径。
- redis_auth: redis密码
- timeout: 读写redis的超时时间，单位毫秒。目前只作用于源redis集群。默认是120000.
- hash: hash函数的名称。只对twemproxy类型起作用。可以是这些值：one_at_a_time、md5、crc16、crc32 (crc32 implementation compatible with libmemcached)、crc32a (correct crc32 implementation as per the spec)、fnv1_64、fnv1a_64、fnv1_32、fnv1a_32、hsieh、murmur、jenkins
- hash_tag: 用两个字符作为key的一部分，来让key有特定的hash值。例如“{}”或“$$”. Hash tag能够将不同的key映射到相同的节点上，只要他们有相同的tag。这个配置只作用于twemproxy。
- distribution: key的分布模式。只作用于twemproxy。可以是下面值：ketama、modula、random。

## common

- listen: 监听的地址和端口（name:port或ip:port）。默认是127.0.0.1:8888
- max_clients：该监听端口客户端最大数量。默认是100
- threads: redis-migrate-tool能够使用的最大的线程数量。默认是cpu核数。
- step: 解析请求的级别。配的越大，迁移越快，消耗内存越多。默认是1
- mbuf_size: Mbuf的大小。默认是512
- noreply: boolean。决定是否要检测目标redis的回复。默认是false。
- source_safe: boolean。是否保护源redis的内存安全。如果设置为true，那么该工具保证一台机器上同一时刻只有一个redis在生成rdb文件。除此之外，设置‘source_safe:true’时，所使用的线程可能会比你设置的要少。默认是true
- dir: 工作目录，用于存储文件（例如rdb文件）。默认是当前目录。
- filter: 过滤key，如果不匹配表达式，则不迁移。表达式Glob-style(通配符)。默认是空。


filter支持通配符表达式：

- h?llo 匹配 hello, hallo 和 hxllo
- h*llo 匹配 hllo 和 heeeello
- h[ae]llo 匹配 hello 和 hallo, 不匹配 hillo
- h[^e]llo 匹配 hallo, hbllo, ... 不匹配 hello
- h[a-b]llo 匹配 hallo 和 hbllo

特殊字符使用“\”进行转义。

## rmt.conf样例

### 单实例到twemproxy

    [source]
    type: single
    servers:
     - 127.0.0.1:6379
     - 127.0.0.1:6380
     - 127.0.0.1:6381
     - 127.0.0.1:6382
    
    [target]
    type: twemproxy
    hash: fnv1a_64
    hash_tag: "{}"
    distribution: ketama
    servers:
     - 127.0.0.1:6380:1 server1
     - 127.0.0.1:6381:1 server2
     - 127.0.0.1:6382:1 server3
     - 127.0.0.1:6383:1 server4
    
    [common]
    listen: 0.0.0.0:8888
    threads: 2
    step: 1
    mbuf_size: 1024
    source_safe: true
    
### twemproxy到redis cluster

    [source]
    type: twemproxy
    hash: fnv1a_64
    hash_tag: "{}"
    distribution: ketama
    servers:
     - 127.0.0.1:6379
     - 127.0.0.1:6380
     - 127.0.0.1:6381
     - 127.0.0.1:6382
    
    [target]
    type: redis cluster
    servers:
     - 127.0.0.1:7379
    
    [common]
    listen: 0.0.0.0:8888
    step: 1
    mbuf_size: 512
    
### redis cluster到redis cluster（abc前缀过滤）

    [source]
    type: redis cluster
    servers:
     - 127.0.0.1:8379
    
    [target]
    type: redis cluster
    servers:
     - 127.0.0.1:7379
    
    [common]
    listen: 0.0.0.0:8888
    filter: abc*

### rdb到redis cluster

    [source]
    type: rdb file
    servers:
     - /data/redis/dump1.rdb
     - /data/redis/dump2.rdb
    
    [target]
    type: redis cluster
    servers:
     - 127.0.0.1:7379
    
    [common]
    listen: 0.0.0.0:8888
    step: 2
    mbuf_size: 512
    source_safe: false    
    
   
### aof到redis cluster

    [source]
    type: aof file
    servers:
     - /data/redis/appendonly1.aof
     - /data/redis/appendonly2.aof
    
    [target]
    type: redis cluster
    servers:
     - 127.0.0.1:7379
    
    [common]
    listen: 0.0.0.0:8888
    step: 2
    
# 查看redis-migrate-tool状态

你可以使用redis-cli连到redis-migrate-tool上。地址和端口就是common配置中设置的。

## info命令

例如，你可以使用 **info** 命令：

    $redis-cli -h 127.0.0.1 -p 8888
    127.0.0.1:8888> info
    # Server
    version:0.1.0
    os:Linux 2.6.32-573.12.1.el6.x86_64 x86_64
    multiplexing_api:epoll
    gcc_version:4.4.7
    process_id:9199
    tcp_port:8888
    uptime_in_seconds:1662
    uptime_in_days:0
    config_file:/ect/rmt.conf
    
    # Clients
    connected_clients:1
    max_clients_limit:100
    total_connections_received:3
    
    # Memory
    mem_allocator:jemalloc-4.0.4
    
    # Group
    source_nodes_count:32
    target_nodes_count:48
    
    # Stats
    all_rdb_received:1
    all_rdb_parsed:1
    all_aof_loaded:0
    rdb_received_count:32
    rdb_parsed_count:32
    aof_loaded_count:0
    total_msgs_recv:7753587
    total_msgs_sent:7753587
    total_net_input_bytes:234636318
    total_net_output_bytes:255384129
    total_net_input_bytes_human:223.77M
    total_net_output_bytes_human:243.55M
    total_mbufs_inqueue:0
    total_msgs_outqueue:0
    127.0.0.1:8888>
    
### **info**命令响应介绍:

#### Server:

- version: redis-migrate-tool版本号
- os：操作系统名称
- multiplexing_api: 多路复用API
- gcc_version: gcc版本
- process_id: redis-migrate-tool的进程id
- tcp_port: redis-migrate-tool监听的tcp端口
- uptime_in_seconds: redis-migrate-tool运行的时长。单位秒。
- uptime_in_days: redis-migrate-tool运行的时长。单位天。
- config_file: redis-migrate-tool配置文件的名称

#### Clients:

- connected_clients: 当前连接的客户端数量
- max_clients_limit: 允许同时连接的最大客户端数量
- total_connections_received: 迄今为止接收的连接数量的总数

#### Group:

- source_nodes_count: 源redis的节点数量
- target_nodes_count: 目标redis的节点数量

#### Stats:

- all_rdb_received: 是否所有源节点的rdb都已接收完毕。
- all_rdb_parsed: 是否所有的源节点rdb文件都已经解析完毕。
- all_aof_loaded: 是否所有的源节点aof文件都已经加载完毕。
- rdb_received_count: 已经接收源redis节点rdb文件的个数。
- rdb_parsed_count: 已经完成解析rdb的个数。
- aof_loaded_count: 已经加载完aof的个数。
- total_msgs_recv: 从源redis接收到的消息总数。
- total_msgs_sent: 发送给目标redis，并已经收到相应的的消息总数。
- total_net_input_bytes: 从源redis接收到的数据总大小
- total_net_output_bytes: 发送给目标redis的数据总大小
- total_net_input_bytes_human: 和total_net_input_bytes相同，转化成可读的格式
- total_net_output_bytes_human: 和total_net_output_bytes相同，转化为可读的格式
- total_mbufs_inqueue: 来自源redis的mbufs的缓存数据（不包括rdb数据）
- total_msgs_outqueue: 待发送给目标redis和已经发送等待响应的消息总数。


# 关闭redis-migrate-tool

## shutdown [seconds|asap]

该命令会干下面几件事：

- 停止从源redis复制数据
- 试图发送redis-migrate-tool缓冲区中的数据给目标集群
- 停止redis-migrate-tool并退出

参数：

Parameter:

- seconds: 在redis-migrate-tool退出之前，它把缓冲区的数据发送给目标redis最多用的时长（秒）。默认是10s 
- asap: 不关心缓冲区的数据，直接退出。

例如，你使用shutdown命令：

    $redis-cli -h 127.0.0.1 -p 8888
    127.0.0.1:8888> shutdown
    OK


# 检查迁移结果（redis_check）

在迁移完数据之后，你可以使用 **redis_check** 命令检查原集群和目标集群的数据。

使用redis_check命令：

    $src/redis-migrate-tool -c rmt.conf -o log -C redis_check
    Check job is running...
    
    Checked keys: 1000
    Inconsistent value keys: 0
    Inconsistent expire keys : 0
    Other check error keys: 0
    Checked OK keys: 1000
    
    All keys checked OK!
    Check job finished, used 1.041s

如果你想检查更多的key，可以使用下面命令：

    $src/redis-migrate-tool -c rmt.conf -o log -C "redis_check 200000"
    Check job is running...
    
    Checked keys: 200000
    Inconsistent value keys: 0
    Inconsistent expire keys : 0
    Other check error keys: 0
    Checked OK keys: 200000
    
    All keys checked OK!
    Check job finished, used 11.962s
    

# 插入一些测试key （redis_testinsert）

使用 **redis_testinsert** 命令：

    $src/redis-migrate-tool -c rmt.conf -o log -C "redis_testinsert"
    Test insert job is running...
    
    Insert string keys: 200
    Insert list keys  : 200
    Insert set keys   : 200
    Insert zset keys  : 200
    Insert hash keys  : 200
    Insert total keys : 1000
    
    Correct inserted keys: 1000
    Test insert job finished, used 0.525s
    
如果你想插入更多的key：

    $src/redis-migrate-tool -c rmt.conf -o log -C "redis_testinsert 30000"
    Test insert job is running...
    
    Insert string keys: 6000
    Insert list keys  : 6000
    Insert set keys   : 6000
    Insert zset keys  : 6000
    Insert hash keys  : 6000
    Insert total keys : 30000
    
    Correct inserted keys: 30000
    Test insert job finished, used 15.486s
    
如果你只想插入string类型的key：

    $src/redis-migrate-tool -c rmt.conf -o log -C "redis_testinsert string"
    Test insert job is running...
    
    Insert string keys: 1000
    Insert list keys  : 0
    Insert set keys   : 0
    Insert zset keys  : 0
    Insert hash keys  : 0
    Insert total keys : 1000
    
    Correct inserted keys: 1000
    Test insert job finished, used 0.024s
    
如果你插入一些特定类型的key：

    $src/redis-migrate-tool -c rmt.conf -o log -C "redis_testinsert string|set|list 10000"
    Test insert job is running...
    
    Insert string keys: 3336
    Insert list keys  : 3336
    Insert set keys   : 3328
    Insert zset keys  : 0
    Insert hash keys  : 0
    Insert total keys : 10000
    
    Correct inserted keys: 10000
    Test insert job finished, used 5.539s