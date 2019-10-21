rdb文件分析工具使用的是github项目

    https://github.com/sripathikrishnan/redis-rdb-tools

# redis-rdb-tools

redis-rdb-tools用于分析rdb文件，可以分析出key的内存使用情况，bigkey等等。

## 功能介绍

1. 解析rdb文件
2. 将rdb文件转为json
3. 分析redis的key的内存使用情况 
4. 找出redis中的BigKey。
5. 根本就是解析rdb文件，能玩出什么花样就看本事了。

## 安装redis-rdb-tools

1.安装Python环境。推荐python3.6+

2.安装rdbtools
    
    pip install -i https://pypi.tuna.tsinghua.edu.cn/simple rdbtools 
    
安装到这一步，已经可以用了，只不过解析比较慢。

3.安装python-lzf （可选，安装后会大幅度增加解析速度）， windows的用户在这步会报错，下面有解决方案

    pip install -i https://pypi.tuna.tsinghua.edu.cn/simple python-lzf
    
4. 安装redis-py （可选，不装的话，redis-memory-for-key命令不能用），个人感觉不需要安装

    pip install -i https://pypi.tuna.tsinghua.edu.cn/simple redis-py


### windows安装python-lzf报错解决方案

报错信息：error: Microsoft Visual C++ 14.0 is required. Get it with "Microsoft Visual C++ Build Tools"

    Looking in indexes: https://pypi.tuna.tsinghua.edu.cn/simple
    Collecting python-lzf
      Downloading https://pypi.tuna.tsinghua.edu.cn/packages/e3/33/b8f67bbe695ccc39f868ae55378993a7bde1357a0567803a80467c8ce1a4/python-lzf-0.2.4.tar.gz
    Installing collected packages: python-lzf
      Running setup.py install for python-lzf ... error
        Complete output from command d:\python37\python.exe -u -c "import setuptools, tokenize;__file__='C:\\Users\\92463\\AppData\\Local\\Temp\\pip-install-p0qoy9m9\\python-lzf\\setup.py';f=getattr(tokenize, 'open', open)(__file__);code=f.read().replace('\r\n', '\n');f.close();exec(compile(code, __file__, 'exec'))" install --record C:\Users\92463\AppData\Local\Temp\pip-record-y8rqxx8i\install-record.txt --single-version-externally-managed --compile:
        running install
        running build
        running build_ext
        building 'lzf' extension
        error: Microsoft Visual C++ 14.0 is required. Get it with "Microsoft Visual C++ Build Tools": https://visualstudio.microsoft.com/downloads/
        
到下面网站下载系统对应的whl文件：

    https://www.lfd.uci.edu/~gohlke/pythonlibs/
    
找到如下列表下载：        

    Python-lzf, bindings to the liblzf library.
        python_lzf‑0.2.4‑cp27‑cp27m‑win32.whl
        python_lzf‑0.2.4‑cp27‑cp27m‑win_amd64.whl
        python_lzf‑0.2.4‑cp35‑cp35m‑win32.whl
        python_lzf‑0.2.4‑cp35‑cp35m‑win_amd64.whl
        python_lzf‑0.2.4‑cp36‑cp36m‑win32.whl
        python_lzf‑0.2.4‑cp36‑cp36m‑win_amd64.whl
        python_lzf‑0.2.4‑cp37‑cp37m‑win32.whl
        python_lzf‑0.2.4‑cp37‑cp37m‑win_amd64.whl
        python_lzf‑0.2.4‑cp38‑cp38‑win32.whl
        python_lzf‑0.2.4‑cp38‑cp38‑win_amd64.whl

下载好后安装：

    pip install python_lzf-0.2.4-cp37-cp37m-win_amd64.whl






# 官方文档翻译

## 简介

**Rdbtools** 是用于解析Redis的 **dump.rdb** 文件的。该解析器就类似xml的sax解析器，很省内存。

除此之外，rdbtools还有如下功能：

1. 遍历所有的数据库和key，生成数据的内存报告。
2. 将dump文件转为json
3. 使用标准的比对工具比对两个rdb文件。

Rdbtools是用Python写的，其他语言也有相似的工具。

关于管理redis，商业支持和一些其他企业特点，可以参见 https://rdbtools.com 提供的GUI。

## 安装rdbtools

前提要求：

1. python-lzf可装可不装，装了之后能极大的提升解析速度，强烈推荐。
2. redis-py可装可不装，你要是需要运行单元测试，那就装

推荐使用PyPI安装：

    pip install rdbtools python-lzf
    
也可使用源码安装：

    git clone https://github.com/sripathikrishnan/redis-rdb-tools
    cd redis-rdb-tools
    sudo python setup.py install

## 命令行使用样例

每次运行rdbtools都需要使用命令来告诉工具应该做什么。合法的命令有：json, diff, justkeys, justkeys, justkeyvals, protocol.

将rdb数据转为json:

    > rdb --command json /var/redis/6379/dump.rdb
    
    [{
    "user003":{"fname":"Ron","sname":"Bumquist"},
    "lizards":["Bush anole","Jackson's chameleon","Komodo dragon","Ground agama","Bearded dragon"],
    "user001":{"fname":"Raoul","sname":"Duke"},
    "user002":{"fname":"Gonzo","sname":"Dr"},
    "user_list":["user003","user002","user001"]},{
    "baloon":{"helium":"birthdays","medical":"angioplasty","weather":"meteorology"},
    "armadillo":["chacoan naked-tailed","giant","Andean hairy","nine-banded","pink fairy"],
    "aroma":{"pungent":"vinegar","putrid":"rotten eggs","floral":"roses"}}]

## 过滤解析后的输出

只处理和正则表达式匹配的key，并且只打印key和value：

    > rdb --command justkeyvals --key "user.*" /var/redis/6379/dump.rdb

    user003 fname Ron,sname Bumquist,
    user001 fname Raoul,sname Duke,
    user002 fname Gonzo,sname Dr,
    user_list user003,user002,user001
    
只处理以“a”开头的hash数据：

    > rdb -c json --db 2 --type hash --key "a.*" /var/redis/6379/dump.rdb

    [{},{
    "aroma":{"pungent":"vinegar","putrid":"rotten eggs","floral":"roses"}}]
    

## 将dump文件转为JSON 

json命令输出的是UTF-8编码的。默认情况下，rdbtools尝试使用UTF-8解析RDB数据，ascii打印不了的字符（比如中文）会使用“\U”表示，UTF-8也解析不了的，使用“\x”表示。尝试解码RDB数据可能会让二进制数据乱码，要避免该问题可以使用 **--escape raw** 选项，或者使用 **-e base64** 编码二进制数据。

解析dump文件并打印JSON：

    > rdb -c json /var/redis/6379/dump.rdb

    [{
    "Citat":["B\u00e4ttre sent \u00e4n aldrig","Bra karl reder sig sj\u00e4lv","Man ska inte k\u00f6pa grisen i s\u00e4cken"],
    "bin_data":"\\xFE\u0000\u00e2\\xF2"}]
    
解析dump文件为raw字节，并打印JSON：

    > rdb -c json /var/redis/6379/dump.rdb --escape raw

    [{
    "Citat":["B\u00c3\u00a4ttre sent \u00c3\u00a4n aldrig","Bra karl reder sig sj\u00c3\u00a4lv","Man ska inte k\u00c3\u00b6pa grisen i s\u00c3\u00a4cken"],
    "bin_data":"\u00fe\u0000\u00c3\u00a2\u00f2"}]
    
## 生成内存报告

使用 **-c memory** 可以生成一个csv报告，它描述了key的大致内存使用情况。 **--byte C** 和 **--largest N** 可以用于输出超过C字节的key，或前N个最大的key。


    # 导出key值大于128字节的数据内存使用情况到memory.csv文件
    > rdb -c memory /var/redis/6379/dump.rdb --bytes 128 -f memory.csv
    > cat memory.csv
    
    database,type,key,size_in_bytes,encoding,num_elements,len_largest_element
    0,list,lizards,241,quicklist,5,19
    0,list,user_list,190,quicklist,3,7
    2,hash,baloon,138,ziplist,3,11
    2,list,armadillo,231,quicklist,5,20
    2,hash,aroma,129,ziplist,3,11
    
生成的csv有下面这些列名：

- database：该key在哪个数据库中
- type：数据类型
- key：key名称
- size_in_bytes：占用内存（字节）
- encoding：RDB编码类型
- num_elements：元素的长度
- len_largest_element：最长的那个元素的长度

注意，这个内存占用是近似的。通常，实际使用内存要比报告中的稍微高一点。

你也可以根据key，数据库索引或数据类型来对报告进行过滤。

内存报告可以帮你轻松的找出因为你应用程序逻辑bug导致的内存泄露问题。它也能帮你去优化Redis的内存使用。

## 找出单个Key的内存使用情况

有时候你只想找出某一个key的内存使用情况，你要是运行整个的内存报告就会很慢。

下面这个例子，你可以是使用 **redis-memory-for-key** 命令：

    > redis-memory-for-key person:1

    > redis-memory-for-key -s localhost -p 6379 -a mypassword person:1
    
    Key 			person:1
    Bytes				111
    Type				hash
    Encoding			ziplist
    Number of Elements		2
    Length of Largest Element	8
    
注意：

1. 这个命令在0.1.3版本之后才支持
2. 该命令依赖 **redis-py** 包


## 对比RDB文件

首先，使用 **--command diff** 选项，然后使用sort工具将其排序。

    > rdb --command diff /var/redis/6379/dump1.rdb | sort > dump1.txt
    > rdb --command diff /var/redis/6379/dump2.rdb | sort > dump2.txt
  
然后使用你自己喜欢的比对工具：

    > kdiff3 dump1.txt dump2.txt
    
为了限制文件的大小，你可以使用 **-key** 选项进行过滤。

## 转为Redis协议的数据流

你可以把RDB文件转为Redis协议的数据流，只要使用 **protocol** 命令即可。


    > rdb --c protocol /var/redis/6379/dump.rdb
    
    *4
    $4
    HSET
    $9
    users:123
    $9
    firstname
    $8
    Sripathi
    
你可以将输出通过管道符发送给netcat，并且重新导入一个子数据集。例如，如果你想将数据切分到两个redis实例中，你可以使用--key选项去选择一个子数据集，然后将其输出到一个运行中的redis中，让其去加载这些数据。[Redis Mass Insert](http://redis.io/topics/mass-insert)这里面有详细信息。

当打印输出的时候， **--escape** 选项的 **printable** 和 **utf8** 参数可以避免不可打印或不可控的字符。

## Python中使用解析器


    from rdbtools import RdbParser, RdbCallback
    from rdbtools.encodehelpers import bytes_to_unicode
    
    class MyCallback(RdbCallback):
        ''' Simple example to show how callback works.
            See RdbCallback for all available callback methods.
            See JsonCallback for a concrete example
        '''
    
        def __init__(self):
            super(MyCallback, self).__init__(string_escape=None)
    
        def encode_key(self, key):
            return bytes_to_unicode(key, self._escape, skip_printable=True)
    
        def encode_value(self, val):
            return bytes_to_unicode(val, self._escape)
    
        def set(self, key, value, expiry, info):
            print('%s = %s' % (self.encode_key(key), self.encode_value(value)))
    
        def hset(self, key, field, value):
            print('%s.%s = %s' % (self.encode_key(key), self.encode_key(field), self.encode_value(value)))
    
        def sadd(self, key, member):
            print('%s has {%s}' % (self.encode_key(key), self.encode_value(member)))
    
        def rpush(self, key, value):
            print('%s has [%s]' % (self.encode_key(key), self.encode_value(value)))
    
        def zadd(self, key, score, member):
            print('%s has {%s : %s}' % (str(key), str(member), str(score)))
    
    
    callback = MyCallback()
    parser = RdbParser(callback)
    parser.parse('/var/redis/6379/dump.rdb')
