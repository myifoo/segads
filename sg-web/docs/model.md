# Session

session数据应该以结构化的形式进行存储，先暂定存储在cassandra中。

每一个原始时间序列都对应唯一的session，session 记录该时间序列统计、建模、分析过程中的配置信息、状态信息、和结果信息。在存储层面
session间相互隔离，如果需要对多个时间序列进行比较性分析的话，在业务层进行处理。这样有利于简化存储结构，避免衍生出复杂的关系。

考虑cassandra面向列、no-schema的特点，尽量将所有sid下挂着的信息都放在session表中。

列名称标识： 
1. ss_xxx: session相关信息；
2. md_xxx: 模型相关信息；
3. dt_xxx: 预测相关信息；
4. ms_xxx: math stats 相关信息；
5. ts_xxx: time series 相关信息；
6.

```json
{
  "ss_id": 1,
  "ts_start_time": 0,
  "md_type": "AutoForecastModel",
  "dt_threshold": ["mape#10", "mase#15"],
  "ts_period": 10,
  "ts_data_step": 1,
  "ts_model": [],
  "ts_detection": [],
  "ts_mathstats": {}
}

```

```
create table session (ss_id int, ts_start_time int, md_type varchar, ts_period int, ts_data_step int, primary key(ss_id));
```

```
create table session (ss_id int, ts_model map<text, map<text, text>>, ts_detection map<text, map<text, text>>, ts_mathstats map<text, text>, primary key(ss_id))
```



# Data

这是用来存储时间序列的表，系统中的时间序列数据有以下多种类型：
1. 原始数据
2. 预测数据
3. 残差
4. 异常数据
5. 差分数据
6. 

.....

同样，基于cassandra no-schema的特性，运行过程中可以动态的往数据表中添加需要的column。
```json
{
    "ss_id": 1,
    "timestamp": 0,
    "raw_value": 1,
    "predict_vaule": 1,
    "residual_value":1,
    "diff_value":1,
    "exception_value":1
}
```

```
create table timeseries (ss_id int, timestamp int, raw_value double, predict_vaule double, residual_value double, primary key(ss_id));
```

# cassandra 数据建模
