## 一、集群模式批量操作会存在的问题

假设现在有三主三从集群如下:

    127.0.0.1:7000> cluster nodes  # 输出省略了一部分内容
    127.0.0.1:7000@17000 master 0-5461      # 卡槽为0-5461
    127.0.0.1:7010@17010 master 5462-10922  # 卡槽为5462-10922
    127.0.0.1:7020@17020 master 10923-16383 # 卡槽为10923-16383
    127.0.0.1:7001@17001 slave
    127.0.0.1:7011@17011 slave
    127.0.0.1:7021@17021 slave
    
现在对该集群进行mset操作，会发现报“(error) CROSSSLOT Keys in request don't hash to the same slot”
    
        
    [root@localhost ~]# redis-cli -c -p 7000
    127.0.0.1:7000> mset key1 value1 key2 value2 key3 value3
    (error) CROSSSLOT Keys in request don't hash to the same slot
    
    
如果使用Jedis客户端对集群操作，会得到以下报错：“redis.clients.jedis.exceptions.JedisClusterException: No way to dispatch this command to Redis Cluster because keys have different slots.”


        JedisCluster jedisCluster = new JedisCluster(hostAndPortSet);
        jedisCluster.mset("key1", "value1", "key2", "value2", "key3", "value3");
        jedisCluster.close();
        
        # 输出：
        Exception in thread "main" redis.clients.jedis.exceptions.JedisClusterException: No way to dispatch this command to Redis Cluster because keys have different slots.
            at redis.clients.jedis.JedisClusterCommand.run(JedisClusterCommand.java:46)
            at redis.clients.jedis.JedisCluster.mset(JedisCluster.java:1441)
            
            
## 二、集群批量报错原因分析

上述两个错误都是在说“key1”、“key2”和“key3”的slot不同，无法进行批量操作。

对于redis集群来说，key要保存在哪个节点上是由key的slot来决定的。也就是说这三个key会被set到不同的节点上。而redis-cli和jedisCluster并没有对这种情况做特殊处理，而是直接抛出异常。

查看key的slot

    127.0.0.1:7000> cluster keyslot key1
    (integer) 9189
    # key1的slot为9189，需要保存在slot范围为5462-10922的“127.0.0.1:7010”节点上
    
    127.0.0.1:7000> cluster keyslot key2
    (integer) 4998
    # key2需要保存在“127.0.0.1:7000”节点上
    
    127.0.0.1:7000> cluster keyslot key3
    (integer) 935
    # key3需要保存在“127.0.0.1:7000”节点上
    
其中，key1和key2,key3需要保存在不同的节点上，所以redis-cli和JedisCluster都会抛出异常，只是描述不一样而已。


## 三、批量操作报错解决方案

### 3.1 将批量操作拆分成单个操作

这个不用多说，把mset改为set，在一个pipeline里的操作也都分开。

#### 优点：实现简单
如果对于性能要求不是很高可以使用该方案。


#### 缺点：性能低
这个缺点也很明显，对于单节点redis来说，批量操作时间=2次网络时间+n次命令操作时间。如图：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190906100405892.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poYW9ob25nZmVpXzM1OA==,size_16,color_FFFFFF,t_70)
> 消耗时间 = 2次网络事件 + n次命令执行时间

集群模式下，消耗时间如图：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190906100946616.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poYW9ob25nZmVpXzM1OA==,size_16,color_FFFFFF,t_70)

最坏的情况下，n次操作都进行moved，那么消耗的时间为
> 消耗的时间 = 4n次网络时间 + n次命令执行时间

最好的情况下，每次请求都正好命中，那么可以忽略1，2步，那么消耗的时间为
> 消耗的时间 = 2n次网络时间 + n次命令执行时间

Jedis会在本地保存集群的卡槽信息，所以基本算是最好的情况，但是2n次网络时间也是无法忍受的。

<br>

### 3.2 客户端计算slots，串行执行
1. 客户端将n个命令操作的key计算slot
2. 根据redis的slots范围将命令放入不同的pipeline中
3. 串行发送批量请求

如图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190906102528967.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poYW9ob25nZmVpXzM1OA==,size_16,color_FFFFFF,t_70)
> 消耗时间为 = 2 * 3次网络 + n次命令执行时间

#### 优点： 性能高
#### 缺点：实现复杂

<br>

代码实现如下：

		# todo

### 3.3 客户端计算slots，并行执行
与3.2类似，只不过请求三个redis节点时是并行的，而非串行。

如图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190906103640570.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poYW9ob25nZmVpXzM1OA==,size_16,color_FFFFFF,t_70)
> 消耗时间 = 2次网络时间 + n次命令执行时间
> 即最慢的线程执行完的时间

#### 优点：速度快，比3.2的还快
#### 缺点：实现更复杂了，需要3个线程资源

代码实现：

		# todo