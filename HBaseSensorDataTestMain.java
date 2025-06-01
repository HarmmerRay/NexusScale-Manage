import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;

import java.io.IOException;

/**
 * HBase传感器数据测试主类
 * 独立运行，不依赖Spring Boot框架
 * 
 * 使用前请确保：
 * 1. HBase服务正在运行
 * 2. Zookeeper服务正在运行
 * 3. iot_sensor_data表已创建并有数据
 * 4. 配置正确的HBase连接参数
 */
public class HBaseSensorDataTestMain {
    
    private static final String TABLE_NAME = "iot_sensor_data";
    private static final String ZK_QUORUM = "192.168.56.11"; // HBase Zookeeper地址
    private static final String ZK_PORT = "2181";             // Zookeeper端口
    
    public static void main(String[] args) {
        System.out.println("=== HBase传感器数据测试开始 ===");
        
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", ZK_QUORUM);
        config.set("hbase.zookeeper.property.clientPort", ZK_PORT);
        config.set("zookeeper.znode.parent", "/hbase");
        
        try (Connection connection = ConnectionFactory.createConnection(config)) {
            
            // 1. 检查表是否存在
            checkTableExists(connection);
            
            // 2. 扫描表数据
            scanSensorData(connection);
            
        } catch (IOException e) {
            System.err.println("HBase连接失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== HBase传感器数据测试结束 ===");
    }
    
    /**
     * 检查表是否存在
     */
    private static void checkTableExists(Connection connection) throws IOException {
        try (Admin admin = connection.getAdmin()) {
            boolean exists = admin.tableExists(TableName.valueOf(TABLE_NAME));
            System.out.println("表 " + TABLE_NAME + " 是否存在: " + exists);
            
            if (!exists) {
                System.out.println("警告: 表不存在，请先创建表和插入测试数据");
            }
        }
    }
    
    /**
     * 扫描传感器数据
     */
    private static void scanSensorData(Connection connection) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(TABLE_NAME))) {
            Scan scan = new Scan();
            scan.setLimit(10); // 限制扫描10条数据
            scan.setCaching(100);
            
            try (ResultScanner scanner = table.getScanner(scan)) {
                int count = 0;
                System.out.println("\n=== 扫描结果 ===");
                
                for (Result result : scanner) {
                    if (result.isEmpty()) {
                        continue;
                    }
                    
                    count++;
                    String rowKey = Bytes.toString(result.getRow());
                    System.out.println("\n第 " + count + " 条数据:");
                    System.out.println("RowKey: " + rowKey);
                    
                    // 解析所有列
                    for (Cell cell : result.listCells()) {
                        String columnFamily = Bytes.toString(CellUtil.cloneFamily(cell));
                        String columnName = Bytes.toString(CellUtil.cloneQualifier(cell));
                        String value = Bytes.toString(CellUtil.cloneValue(cell));
                        long timestamp = cell.getTimestamp();
                        
                        System.out.println("  列族: " + columnFamily);
                        System.out.println("  列名: " + columnName);
                        System.out.println("  值: " + value);
                        System.out.println("  时间戳: " + timestamp);
                        
                        // 尝试解析JSON中的数值
                        Double sensorValue = parseValueFromJson(value);
                        if (sensorValue != null) {
                            System.out.println("  解析后的数值: " + sensorValue);
                        }
                    }
                }
                
                if (count == 0) {
                    System.out.println("未找到任何数据");
                } else {
                    System.out.println("\n总共扫描到 " + count + " 条数据");
                }
            }
        }
    }
    
    /**
     * 从JSON字符串中解析传感器值
     * 例如：{"temperature":{"value":80.2}} -> 80.2
     */
    private static Double parseValueFromJson(String jsonValue) {
        try {
            if (jsonValue.contains("\"value\":")) {
                String valueStr = jsonValue.substring(jsonValue.indexOf("\"value\":") + 8);
                valueStr = valueStr.substring(0, valueStr.indexOf("}")).trim();
                return Double.parseDouble(valueStr);
            }
        } catch (Exception e) {
            // 解析失败，忽略
        }
        return null;
    }
} 