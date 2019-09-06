## 一、什么是主从复制

在集群模式下，主节点会将自己的数据同步给从节点，这个就是主从复制。

主从复制的作用：

1. 为数据提供了一个或多个副本，可以实现高可用
2. 扩展了读性能，可以实现读写分离

要点：

1. 一个Master可以有多个Slave，但是一个Slave只能有一个Master
2. 一个Slave还可以有Slave
3. 数据流向是单向的，只能是Master到Slave


## 二、主从复制配置

### 2.1 命令配置

    # 在6379（主）上设置key1的值为value
    127.0.0.1:6379> set key1 value1
    OK
    
    # 在6380（从）查询不到该key1。因为其还没成为6379的从
    127.0.0.1:6380> get key1
    (nil)
    
    # 使6380（从）成为6379（主）的从节点。命令为salveof ip port
    127.0.0.1:6380> slaveof 127.0.0.1 6379
    OK
    
    # 在6380（从）可以获得到6379（主）的key1的value，说明进行了主从复制
    127.0.0.1:6380> get key1
    "value1"
    
    # 不再将6380作为从节点 
    127.0.0.1:6380> slaveof no one
    OK
    
    # 6380依然可以获取到key1的值 
    127.0.0.1:6380> get key1
    "value1"
    
    # 6380无法获取到6379设置的新值，因为6380已经不是6379的从节点了，所以不还再进行主从同步
    127.0.0.1:6379> set key2 value2
    OK
    127.0.0.1:6380> get key2
    (nil)
 
> 如果从节点执行slave no one脱离主节点，从节点不会清除当前数据。
    
    
### 2.2 配置文件

修改配置文件，增加如下两个配置

    
    # 将该redis作为ip机器port端口redis的从
    slaveof <masterip> <masterport>
    
    # 使得该redis只能进行读操作，避免出现主从数据不一致
    slave-read-only yes
    

## 三、全量复制基本原理

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190904112631515.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poYW9ob25nZmVpXzM1OA==,size_16,color_FFFFFF,t_70)

1. slave发送“psync ? -1”请求到Master，让master为其同步数据。 ? 代表runId, -1代表偏移量。因为从节点现在不知道主节点的runId和偏移量，所以是问号和-1.
2. master响应slave，并返回“+FULLRESYNC {runId} {offset}”。
3. slave保存masterInfo
4. master执行bgsave进行rdb文件的生成。
5. master发送RDB文件给slave
6. master将缓冲区的内容发送给slave。缓冲区的内容就是发送rdb文件时，新写入master的内容。
7. slave清除之前的数据
8. slave加载rdb文件中的数据

   
### 全量复制的开销

1. bgsave。 第4步中，master进行bgsave，会消耗时间，cpu，内存，IO等
2. rdb文件网络传输时间。 第5步中，master将rdb发送给slave会消耗时间，网络带宽资源。
3. 从节点清空数据时间。  第7步中， slave清除旧数据会消耗一定时间。
4. 从节点加载RDB的时间。 第8步中，从节点加载rdb会消耗一定时间。
5. 可能的AOF重写时间。  第8步之后，如果开启了AOF，则会引起AOF的重写机制。


## 四、部分复制基本原理
假设在全量复制的过程中出现了网路抖动，那么这段时间的master的数据变化Slave是无法知道的。在redis2.8之前，会再次进行全量复制。但是在redis2.8及以后提供了部分复制的功能。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190904113436974.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poYW9ob25nZmVpXzM1OA==,size_16,color_FFFFFF,t_70)

1. Slave到Master的连接丢失
2. Master将自己的数据变更写入缓冲区 
3. slave向Master再一次进行TCP连接
4. slave发送参数“pysnc {slaveOffset} {slaveRunID}”给Master，告知Master当前的偏移量
5. 如果Slave的Offset在缓冲区内，那么就会进行部分复制。如果不在，说明slave已经错过了较多的数据，需要再次进行全量复制。
6. 进行部分复制。Master按照偏移量将缓冲区内的数据发送给Slave

## 五、开发与运维常见问题
### 读写分离
 读写分离是指，在Master端进行数据的写入，在Slave端进行数据的读取，以此来减少服务器压力。

可能遇到的问题：

- 复制数据延迟：主从复制需要一定时间（虽然很短），但是也会造成一定的时间差
- 读到过期数据：读写分离情况下，从节点没有权限操作数据，所以如果数据过期后，主删除数据后没有及时同步给从，读取的时候，就会读取到过期数据。


### 配置不一致

- maxmemory不一致：比如，Master最大内存为4G，Slave为2G，当Slave拿到rdb文件后，发现内存不够用，就会采用maxmemory-policy的淘汰策略，淘汰一部分数据。这就造成了主从数据不一致。如果你更倒霉，此时发生了主从切换，数据少的从节点变为了主节点，那么就会造成永久的数据丢失。
- 数据结构优化参数不一致：例如hash-max-ziplist-entries等。数据结构优化的参数不一致会导致同一个数据在主从所使用的内存是不一样的，这也会间接导致上面的那个问题。


### 规避全量复制
##### 1. 第一次全量复制
这个不可避免，但是可以使用其他方案减小其带来的开销。
比如：1. 在夜间进行slave的接入。 2. 主节点的maxmemory不要太大。

##### 2.节点运行ID不匹配
当主节点RunId发生变化时（比如主节点重启），从节点发现主节点的runId发生变化后，会再次进行全量复制。
使用故障转移可以避免该情况发生，比如主从切换

##### 3. 复制积压缓冲区不足
部分复制是将主节点缓冲区中的内容发送给从节点，但如果从节点的偏移量不在主节点的缓冲区中（比如写入量太大），就会造成全量复制。
解决办法：增大主节点缓冲区（更改配置rel_backlog_size，默认是1M）


### 规避复制风暴
复制风暴指的是，如果一个Master有多个Slave，比如Master挂了后重启，这个时候所有的Slave都会同时找Master去进行全量同步，这个就是复制风暴。

##### 1. 单节点的复制风暴
主节点节点重启之后，多个节点同时找主节点全量复制。
解决方法：更换复制拓扑。如图：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190904142627184.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poYW9ob25nZmVpXzM1OA==,size_16,color_FFFFFF,t_70)
> 虽然这个可以解决复制风暴的问题，但是还存在其他问题，比如Slave1没有做高可用。可以再扩展个Slave2

##### 2. 单机器复制风暴
如果一个主机上部署的全是Master，也会造成复制风暴
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190904142938687.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poYW9ob25nZmVpXzM1OA==,size_16,color_FFFFFF,t_70)
解决办法：将主节点部署在不同的主机上