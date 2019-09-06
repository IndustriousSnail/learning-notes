#####  今天在本地虚拟机搭建了一个redis集群，但是发现奇怪的现象，在虚拟机外边可以redis-cli可以连接成功，并且Jedis单独连接也可以连接成功，但是就是用JedisCluster连接不成功。报如下错误：
“JedisConnectionException: Could not get a resource from the pool”

	Exception in thread "main" redis.clients.jedis.exceptions.JedisConnectionException: Could not get a resource from the pool
		at redis.clients.util.Pool.getResource(Pool.java:53)
	Caused by: redis.clients.jedis.exceptions.JedisConnectionException: java.net.ConnectException: Connection refused: connect
		at redis.clients.jedis.Connection.connect(Connection.java:207)
	Caused by: java.net.ConnectException: Connection refused: connect
		at java.net.DualStackPlainSocketImpl.waitForConnect(Native Method)

	# 省略了一部分错误输出

好多人和我遇到了同样的问题，但是网上找了好长时间，都是乱七八糟的。

## 解决方法：
#### 注： 我虚拟机的IP为192.168.79.134，下面出现的所有改ip换成你们自己的IP

#### 1. 停止redis集群，删除 ./dir 配置目录下的所有文件

	[root@localhost data]# ls
	7000.log  7010.log  7020.log  dump-7000.rdb  dump-7010.rdb  dump-7020.rdb  nodes-7000.conf  nodes-7010.conf  nodes-7020.conf
	7001.log  7011.log  7021.log  dump-7001.rdb  dump-7011.rdb  dump-7021.rdb  nodes-7001.conf  nodes-7011.conf  nodes-7021.conf

先把集群生成的默认nodes.conf给删了，还原配置。

#### 2. 每个节点的配置文件都增加bind配置
	
	#端口7000
	port 7000
	# 以守护进程方式启动
	daemonize yes
	# 数据存放目录，如rdb文件，日志文件等
	dir "./data"
	# 日志文件名称
	logfile "7000.log"
	# db文件名称
	dbfilename "dump-7000.rdb"
	# 设置该redis为集群节点
	cluster-enabled yes
	# 集群本地的配置文件，对集群的配置进行一个记录
	cluster-config-file nodes-7000.conf
	# 如果这个配置为yes，那么集群中只要有一个节点挂了，整个集群就不可用了
	cluster-require-full-coverage no
	# 关闭保护模式，否则jedis无法访问
	protected-mode no
	
	# 增加该配置 # 增加该配置 # 增加该配置 # 增加该配置 # 增加该配置 # 增加该配置 # 增加该配置 # 增加该配置
	# 配置当前网卡的ip
	bind 192.168.79.134

> 我在虚拟机外边用 192.168.79.134 这个IP连接的虚拟机，你们用哪个就配置哪个

#### 3. 修改启动脚本，所有都用具体ip（不能用127.0.0.1）
比如：
	
	./redis/src/redis-cli -p 7000 cluster meet 127.0.0.1 7001	 错错错错错
	
	./redis/src/redis-cli -h 192.168.79.134 -p 7000 cluster meet 192.168.79.134    对对对对对
	./redis/src/redis-cli -h 192.168.79.134 -p 7000 cluster addslots ${slot}   对


#### 4. 启动redis集群，再看就发现好了

<br><br><br><br>

# 原因分析
我的代码为

	    public static void main(String[] args) throws IOException {
	        Set<HostAndPort> hostAndPortSet = new HashSet<>();
	        hostAndPortSet.add(new HostAndPort("192.168.79.134", 7000));
	        hostAndPortSet.add(new HostAndPort("192.168.79.134", 7001));
	        hostAndPortSet.add(new HostAndPort("192.168.79.134", 7010));
	        hostAndPortSet.add(new HostAndPort("192.168.79.134", 7011));
	        hostAndPortSet.add(new HostAndPort("192.168.79.134", 7020));
	        hostAndPortSet.add(new HostAndPort("192.168.79.134", 7021));
	        JedisCluster jedisCluster = new JedisCluster(hostAndPortSet);
	        jedisCluster.set("key3", "value3");  # 该处报错
	        jedisCluster.close();
	    }

根据报错异常堆栈找到具体异常处，在该处打断点后发现
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190906151244642.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poYW9ob25nZmVpXzM1OA==,size_16,color_FFFFFF,t_70)
#### ？？？？ 我明明输入的ip是192.168.79.134，为什么连接的时候是用127.0.0.1连接的，那肯定连接不上。

#### 这个127.0.0.1是从哪来的。 答案是Redis集群告诉Jedis的
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190906152448966.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poYW9ob25nZmVpXzM1OA==,size_16,color_FFFFFF,t_70)
> redis集群在做meet操作时，因为是本机，所以走的是lo网卡（127.0.0.1），所以节点之间都认为对方的IP地址是127.0.0.1。

Jedis时这么做的：

    1.连接Redis集群（这步是成功的，所以这行代码没报错。）
    JedisCluster jedisCluster = new JedisCluster(hostAndPortSet);
    
    2.进行操作时，Jedis会从连接池中拿一个Jedis连接
    return connectionPool.getResource(); // JedisSlotBasedConnectionHandler.java 66行
    
    3.如果连接池中没有连接，则去new一个新的连接
    return internalPool.borrowObject();  // Pool.java  49行，报错行
    
    // 如果连接池中没有连接，则去new一个新的连接
    // org.apache.commons.pool2.impl.GenericObjectPool.class  163行
    if (p == null) {
        p = this.create();  // 真正报错的地方
        if (p != null) {
            create = true;
        }
    }

#### 所以为了让Redis能返回给Jedis正确的IP地址，需要做如下两件事情：

1. 让Redis不走lo网卡，而是走实际IP的地址的网卡

    使用bind配置， bind 192.168.79.134意思是，所有的Redis请求都从这个IP地址的网卡进。
    
    这样的话，redis-cli就不能直接访问了，即使在虚拟机中，也必须指定IP才能访问Redis（如果不指定-h，则默认127.0.0.1走的还是lo网卡，但是bind配置限制死了，不能走lo网卡）。
    所以要修改启动脚本和配置文件。
    
2. 删除自动生成的nodes-port.conf文件

    Redis对于其他节点的ip地址是在这个配置文件下记录的，所以得删了，让他重新生成一次。