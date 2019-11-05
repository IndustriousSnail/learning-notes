RocketMQ4.4.0版本后加入了访问权限控制。可以通过配置IP白名单，账号密码的方式对权限进行有效的控制。

本篇主要对RocketMQ ACL的使用进行介绍。建议先参考“丁威”大神对RocketMQ ACL的介绍文章：

    https://mp.weixin.qq.com/s/el7miaJGILP0wYjC3v3Twg
    https://blog.csdn.net/prestigeding/article/details/94317946
    https://www.jianshu.com/p/7c9b20518800
    
这些应该都是他本人的博客。

# 一、Rocket ACL配置

## 1. 修改broker.conf配置文件

首先需要开启权限访问：

    aclEnable=true
    

例如：        

    brokerClusterName = DefaultCluster
    brokerName = broker-a
    brokerId = 0
    deleteWhen = 04
    fileReservedTime = 48
    brokerRole = ASYNC_MASTER
    flushDiskType = ASYNC_FLUSH
    
    # ...
        
    # 开启权限访问配置        
    aclEnable=true
    
## 2.配置plain_acl.yml文件

plain_acl.yml里面配置账号密码以及账号对应的访问权限，需要将plain_acl.yml放到conf（和broker.conf在一个目录下）目录下

里面写入如下内容：

    globalWhiteRemoteAddresses:
    
    accounts:
    - accessKey: RocketMQ
      secretKey: 12345678
      whiteRemoteAddress:
      admin: false
      defaultTopicPerm: DENY
      defaultGroupPerm: SUB
      topicPerms:
      - topicA=DENY
      - topicB=PUB|SUB
      - topicC=SUB
      groupPerms:
      # the group should convert to retry topic
      - groupA=DENY
      - groupB=PUB|SUB
      - groupC=SUB
    
    - accessKey: rocketmq2
      secretKey: 12345678
      whiteRemoteAddress: 
      # if it is admin, it could access all resources
      admin: true
        
这个是源码示例中给的，该例子中，配置了两个账号。两个账号的权限描述如下：

- RocketMQ账号：
  - 账号：RocketMQ
  - 密码：12345678
  - whiteRemoteAddress: 无IP白名单，即所有IP都可以访问
  - admin: false  不是管理员
  - defaultTopicPerm: DENY 默认的Topic权限为拒绝
  - defaultGroupPerm: SUB  默认Group权限为可订阅
  - topicPerms:
  - groupPerms:
  

## 3.重启MQ

# 二、生产者代码编写

## 


# 不生效可能原因

1. 启动broker时，没有指定broker.conf配置文件。
2. plain_acl.yml文件格式有错误，可以使用在线yaml校验工具校验下

    http://www.bejson.com/validators/yaml/       

3. 查看MQ版本是否大于4.4.0
 