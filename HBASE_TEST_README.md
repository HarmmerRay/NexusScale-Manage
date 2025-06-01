# HBase IoT传感器数据查询测试

本项目提供了从HBase 2.2.2服务中查询`iot_sensor_data`表数据的完整解决方案。

## 项目结构

### 主要文件

1. **配置文件**
   - `src/main/resources/application.properties` - Spring Boot应用配置
   - `src/main/java/com/nexuscale/nexusscalemanage/config/HBaseConfig.java` - HBase连接配置类

2. **实体类**
   - `src/main/java/com/nexuscale/nexusscalemanage/entity/IoTSensorData.java` - IoT传感器数据实体

3. **服务类**
   - `src/main/java/com/nexuscale/nexusscalemanage/service/HBaseSensorDataService.java` - HBase数据查询服务

4. **测试类**
   - `src/test/java/com/nexuscale/nexusscalemanage/HBaseSensorDataTest.java` - Spring Boot集成测试
   - `HBaseSensorDataTestMain.java` - 独立测试类（不依赖Spring Boot）

## HBase配置信息

```properties
# HBase Zookeeper配置
hbase.zookeeper.quorum=192.168.56.11
hbase.zookeeper.property.clientPort=2181
hbase.table.name=iot_sensor_data
hbase.column.family=cf1
```

## 数据格式

根据提供的数据样例，HBase中的数据格式为：
- **RowKey**: `13_1748753523972`
- **列族**: `cf1`
- **列名**: `temperature` (传感器类型)
- **数据值**: `{"temperature":{"value":80.2}}` (JSON格式)
- **时间戳**: `1748753524413`

## 使用方法

### 方法一：Spring Boot集成测试

1. **启动HBase服务**
   确保HBase和Zookeeper服务正在运行在`192.168.56.11:2181`

2. **运行测试**
   ```bash
   # 运行所有测试
   mvn test -Dtest=HBaseSensorDataTest
   
   # 运行特定测试方法
   mvn test -Dtest=HBaseSensorDataTest#testScanAllSensorData
   mvn test -Dtest=HBaseSensorDataTest#testScanWithLimit
   mvn test -Dtest=HBaseSensorDataTest#testScanByRowKeyPrefix
   ```

3. **测试方法说明**
   - `testScanAllSensorData()` - 扫描表中所有数据
   - `testScanWithLimit()` - 限制扫描数量（默认5条）
   - `testScanByRowKeyPrefix()` - 根据行键前缀扫描（默认前缀"13"）
   - `testTableExists()` - 检查表是否存在
   - `comprehensiveTest()` - 综合测试

### 方法二：独立测试类

1. **编译项目**
   ```bash
   mvn compile
   ```

2. **运行独立测试**
   ```bash
   # Windows
   java -cp "target/classes;target/lib/*" HBaseSensorDataTestMain
   
   # Linux/Mac
   java -cp "target/classes:target/lib/*" HBaseSensorDataTestMain
   ```

3. **或者直接在IDE中运行**
   直接运行`HBaseSensorDataTestMain.java`的main方法

## 主要功能

### HBaseSensorDataService 提供的方法

1. **scanAllSensorData()** - 扫描所有传感器数据
   ```java
   List<IoTSensorData> allData = hBaseSensorDataService.scanAllSensorData();
   ```

2. **scanWithLimit(int limit)** - 限制数量扫描
   ```java
   List<IoTSensorData> limitedData = hBaseSensorDataService.scanWithLimit(10);
   ```

3. **scanByRowKeyPrefix(String prefix)** - 根据行键前缀扫描
   ```java
   List<IoTSensorData> prefixData = hBaseSensorDataService.scanByRowKeyPrefix("13");
   ```

4. **isTableExists()** - 检查表是否存在
   ```java
   boolean exists = hBaseSensorDataService.isTableExists();
   ```

### 数据解析功能

- 自动解析JSON格式的传感器值：`{"temperature":{"value":80.2}}` → `80.2`
- 提取列族、列名、时间戳等完整信息
- 支持多种传感器类型（temperature、humidity、pressure等）

## 输出示例

```
=== 开始测试扫描所有传感器数据 ===
扫描结果总数: 100
数据 1: RowKey=13_1748753523972, 列族=cf1, 列名=temperature, 时间戳=1748753524413, 原始值={"temperature":{"value":80.2}}, 解析值=80.2
数据 2: RowKey=14_1748753524001, 列族=cf1, 列名=humidity, 时间戳=1748753524500, 原始值={"humidity":{"value":65.5}}, 解析值=65.5
...
=== 测试扫描所有传感器数据结束 ===
```

## 注意事项

1. **网络连接**: 确保能够访问HBase服务器`192.168.56.11:2181`
2. **表结构**: 确保`iot_sensor_data`表已创建并包含数据
3. **依赖版本**: 使用HBase 2.2.2客户端与服务端版本匹配
4. **内存配置**: 大量数据扫描时注意JVM内存设置

## 故障排除

1. **连接失败**
   - 检查HBase和Zookeeper服务状态
   - 验证网络连通性：`telnet 192.168.56.11 2181`
   - 确认防火墙设置

2. **表不存在**
   - 在HBase shell中确认表存在：`list`
   - 检查表名拼写：`iot_sensor_data`

3. **无数据返回**
   - 确认表中有数据：`scan 'iot_sensor_data', {LIMIT => 5}`
   - 检查列族配置是否正确

4. **JSON解析失败**
   - 确认数据格式符合预期：`{"sensorType":{"value":numericValue}}`
   - 查看日志中的解析警告信息

## 扩展功能

可以根据需要扩展以下功能：
- 按时间范围查询
- 按传感器类型过滤
- 数据聚合统计
- 实时数据监控
- 数据导出功能 