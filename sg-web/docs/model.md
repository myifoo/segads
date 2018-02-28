# cassandra 数据建模
Cassandra3.0之后的版本，与之前的版本有很大的修改，尤其是 data model 及 对cql的支持方面，schema-less的数据结构不再合理了。

## Session
> name, category, key |  int_v, str_v, set_v, map_v, list_v

name: session 名称，
category: main, model, detection, math, 

name.category.key
session-001.main.attach -> _list : 这里的list_value中保存的是Data表中的 metric_uri;
session-001.main.properties -> _map : 
session-001.main.status -> _int :
session-001.model.arima -> _map : 
session-001.math.

Session 的目的就是管理，对配置的管理，对规则的管理，对状态的管理, 对结果的管理 .....

这样的数据结构如何？

1. 程序运行过程中，不再需要动态改变column；
2. 考虑到了 partition key， cluster key 的存储机制；
3. 对于数据的存/取时的结构由业务层通过代码来实现，比如 type 为 properties 就使用 str_value； data 就是用
int_value; model 就使用 map_value; attach 就使用 list_value； 

session 通过 attach 来挂载到不同的原始数据上，数据存储在data表中，是独立且 immutable 的。


Data 
> id, time，type | value

id 是数据的唯一标识，比如对于指标，可以是如下形式： 
1. host.cpu.temperature.key -> host.cpu.temperature.100001
2. key -> 100001
3. name -> wang_er_gou

time 就是时间信息，时间戳对应的秒数值；

type 是数据类型，raw 是固有的，用来存原始数据，其他的可以动态扩展，比如预测数据，残差，异常数据， 差分数据 ...
type 名称规范： ( session name + data type ) -> ( session-001.diff)

