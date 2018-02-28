# 1 数据准备
数据提交、存储、更新、检索、展示

数据的有效性
数据的完整性

很多功能优先级不高，第一阶段先处理最简单（无异常）的情况！

## 1.1 数据上传

方式： http 接口 + csv/json

这不是最有效、最高效的数据上传方式，但应该能满足目前的要求。

时间序列数据在更新时，具有零碎、大量的特点，比如10000个指标，每分钟更新一次数据，平均速率为200/s，通过接口的方式进行数据更新
肯定不合适，最好的方式是FIFO消息队列，后续可以增加MQ模块，能更好的支持异步及大批量的数据更新。

## 1.1.1/segads/data/create

> curl.exe -i -X POST -F "file=@tree.csv" -F "name=tree" http://localhost/segads/data/create?id=001

需要注意的是，windows 环境里 curl 的 -F 参数需要使用双引号，与 linux 不同。

1. csv 只包含数据，不包含 header； 目前对于非法的数据格式没有做检验和验证；
2. 


## 1.1.2 /segads/data/update

```json
{
  "id_001": [
    [1001, "2.78"],
    [1002, "2.99"]
  ],
  "id_002": [
      [1001, "2.78"],
      [1002, "2.99"]
  ]
}
```

时间序列数据的更新是指在已经存在的时间序列上，添加新的数据（时间序列不提供对数据点修改的接口，没有此需求），json 的格式如示例所示。
一次可以对若干数据序列进行更新。

> curl.exe -i -X POST -H "Content-Type:application/json" -d "{'id_001': [[1001, '2.78'],[1002, '2.99']],'id_002': [[1001, '2.78'],[1002, '2.99']]}" http://localhost/segads/data/update

1. 当 update id 不存的数据列时，会在数据库中生成新的 id 数据记录；
2. 当 update 的数据列已存在时（也就是 primary key 已存在），会更新 value 的值； 
3. 对外接口中无需指定 type，始终设为 'raw'；

### 1.1.3 /segads/data/get/{id}

> curl.exe -i -X GET http://localhost/segads/data/id_001

返回值
```json
{
  "id_001":[
      [1001,5],
      [1002,2.99]
    ]
}
```

## 1.2 数据展示

### 1.2.1 push data

> push test https://data.jianshukeji.com/jsonp?filename=json/usdeur.json&callback=?

在命令行中输入上述命令，会将url对应的json数据 push 到数据库中；

### 1.2.2 show data

> show test

# 数据转换

数据规范化、填充、筛除无效数据等等

# 数据统计分析、建模与预测
数据的统计描述
时间序列建模：指数平滑，ARIMA
数值预测
数据更新后的模型优化



# 异常检测
异常检测建模与模型匹配策略
数据更新后的模型优化

# 结果存储与展示
预测模型
异常模型
统计信息

# 其他
数据库模型： 时间序列数据不能存储在传统的关系型数据库中，数据表的设计思路也完全不同，需要特定的schema。
规则管理：数据，模型，规则 （系统自带若干规则，可以将模型挂着到特定规则上，然后按照预制的模型进行预测和检测。。。）
会话管理：手动建模时，通过会话来管理数据和模型信息；

## 数据库模型

segads keyspace
```sql
CREATE KEYSPACE IF NOT EXISTS segads  
      WITH REPLICATION = {'class': 'SimpleStrategy','replication_factor' : 1}
```

session table
```sql
CREATE TABLE IF NOT EXISTS session (
        name text, 
        category text, 
        key text, 
        int_v int, 
        str_v text, 
        set_v set<text>, 
        list_v list<text>, 
        map_v map<text, text>, 
        PRIMARY KEY (name, category, key));
```

data table
```sql
CREATE TABLE IF NOT EXISTS data (
        id text, 
        type text, 
        time bigint, 
        value double, 
        properties map<text, text> static, 
        primary key(id, type, time)) 
        WITH COMPACT STORAGE AND CLUSTERING ORDER BY (time DESC);
```

table_options : 后续再优化，包括排序，cluster 等等，对性能会有影响。

1. time 为 bigint 类型，对应 java 中的 long，用于存储 UTC 时间，可以存储秒数，也可以存储毫秒数，由数据使用者自行管理；
2. value 列为 double 类型，因为实际应用中可能是浮点数，也可能是整型，当存储数据流之类的指标时，32位可能不够；
3. value 数据在进行转换时，如从 json 转为 double 变量等情况，有可能会发生数据精度丢失，暂时不做处理；

> CREATE TABLE IF NOT EXISTS data (id text, type text, time bigint, value double, properties map<text, text> static, primary key(id, type, time)) WITH CLUSTERING ORDER BY (time DESC);


