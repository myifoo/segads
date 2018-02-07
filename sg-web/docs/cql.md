# Simple statements

SimpleStatement 适用于执行临时性的cql语句，每次都会重新解析，不缓存。

```
SimpleStatement statement = new SimpleStatement(
    "SELECT value FROM application_params WHERE name = 'greeting_message'");
session.execute(statement);

session.execute("SELECT value FROM application_params WHERE name = 'greeting_message'"); // a vonvenient shotcut way to execute simple statement
```

如果query语句将被多次重复的调用，应考虑使用 prepared statement。

## 参数绑定

1. 按位置
```
session.execute(
    "SELECT value FROM application_params WHERE name = ? and version = ?",
    "segads", "1.0");
```

参数个数应该与query语句中预留的位置相符，且类型匹配，否则会在cassandra server端产生InvalidQueryException异常！

```
session.execute(
        "INSERT INTO bigints (b) VALUES (?)",
        1);
```
此处会产生InvalidQueryException异常，因为driver会将1序列化为int类型，与bigints不匹配，正确的写法如下：
```
session.execute(
        "INSERT INTO bigints (b) VALUES (?)",
        1L);
```

同样需要注意的是java String会被driver序列化为varchar，当目标列的类型为ascii时，会产生异常，唯一的做法是对参数值手动序列化，如下：
```
ProtocolVersion protocolVersion = cluster.getConfiguration().getProtocolOptions().getProtocolVersionEnum();
ByteBuffer bytes = DataType.ascii().serialize("Touché sir, touché...", protocolVersion);
session.execute(
        "INSERT INTO ascii_quotes (id, t) VALUES (?, ?)",
        1, bytes);
```

2. 按名称
```
// Just a convenience to build a java.util.Map with a one-liner
import com.google.common.collect.ImmutableMap;

String paramName = ...
session.execute(
    "SELECT value FROM application_params WHERE name = :n",
    ImmutableMap.<String, Object>of("n", paramName));
```

# Prepared statements

有些query语句会多次调用，这时应考虑使用 PreparedStatement。
```
PreparedStatement prepared = session.prepare(
  "insert into product (sku, description) values (?, ?)");

BoundStatement bound = prepared.bind("234827", "Mouse");
session.execute(bound);

session.execute(prepared.bind("987274", "Keyboard"));
```

在prepare query语句时，cassandra 解析并缓存结果，然后返回该statement的标识符（PreparedStatement），再execute该query语句时将会更高效。

PreparedStatement 是 thread-safe 的，在同一应用中，对同一 query 语句只需要执行一次 prepare。

## 参数绑定

通过 bind 来绑定参数，并生成 BoundStatement，然后execute。

```
BoundStatement bound = ps1.bind("324378", "LCD screen");
```

也可以在生成 BoundStatement 后，通过 setter 来显示的绑定参数。

```
// Positional setters:
BoundStatement bound = ps1.bind()
  .setString(0, "324378")
  .setString(1, "LCD screen");

// Named setters:
BoundStatement bound = ps2.bind()
  .setString("s", "324378")
  .setString("d", "LCD screen");
```

如果想重用 bound 变量，可以调用 unset 方法来删除绑定。
```
// Using the unset method to unset previously set value.
// Positional setter:
bound.unset("description");

// Named setter:
bound.unset(1);
```
需要注意的是 BoundStatement 是非线程安全的（not thread-safe）。

## 避免 prepare 'SELECT *' 语句
datastax driver 和 cassandra 都会维护一份 PreparedStatement 与 metadata 的映射，如果列族发生更改，例如添加或删除了一些column，Cassandra目前并没有一个很好的机制去注销已存在的metadata，因此 driver 也不会获取变更信息，在读取 rows 时将会发生错误。

推荐的做法是，不对'SELECT *' prepare；应该列出自己期望的列名，例如'SELECT a, b, c FORM table1'.

# QueryBuilder

# ObjectMapping