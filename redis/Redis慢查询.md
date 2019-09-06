## 一、什么是Redis慢查询

当某条Redis命令执行过慢时（达到某一个阈值），该条命令就属于慢查询，
会被记录到慢查询日志中去。

注意：执行过慢指的是开始执行到执行结束，是不包括排队时间（等待之前的命令）和网络时间的。

## 二、慢查询相关配置

#### slowlog-log-slower-than

    # 当命令执行时间超过该配置时（单位微秒），则记录慢查询日志
    slow-log-slower-than=10000   # 默认值为10s
    # 当配置为0时，表示所有命令都记录慢查询日志
    slow-log-slower-than=0
    # 当配置小于0时，表示所有命令都不记录慢查询日志
    slow-log-slower-than=-1
    
    
#### slowlog-max-len

    # 慢查询队列的默认大小，即最多存储多少慢查询日志。
    slowlog-max-len=128  # 默认128
    
    # 当存储满后，再进来的新的会替代旧的，那些旧的就查不到了。
    
    
## 三、慢查询演示

    # 1. 设置记录所有命令都记录慢查询日志
    127.0.0.1:6379> config set slowlog-log-slower-than 0
    OK
    # 2. 随便执行一个命令
    127.0.0.1:6379> set key1 value1
    OK
    # 3. 查看慢查询日志。slowlog get [n]，获取最近的n条慢查询日志
    127.0.0.1:6379> slowlog get
    1) 1) (integer) 5               # 每个慢查询日志的唯一渐进(递增的)标识
       2) (integer) 1567404714      # 该命令被执行时的时间戳
       3) (integer) 6               # 执行该命令所消耗的的时间（微秒）
       4) 1) "set"                  # 执行的命令以及其参数组成的数组
          2) "key1"
          3) "value1"
          
       # Redis4.0之后，还会包含5)和6)，分别是客户端IP:port和客户端名称
       
    # 输出当前慢查询日志的数量
    127.0.0.1:6379> slowlog len
    (integer) 13
    
    # 清空慢查询日志
    127.0.0.1:6379> slowlog reset
    OK

命令详情可以参考官方文档<br>
> https://redis.io/commands/slowlog 
    