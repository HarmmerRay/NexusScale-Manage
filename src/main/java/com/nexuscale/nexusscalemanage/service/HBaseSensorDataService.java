package com.nexuscale.nexusscalemanage.service;

import com.nexuscale.nexusscalemanage.entity.IoTSensorData;
import com.nexuscale.nexusscalemanage.config.HBaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * HBase传感器数据服务类
 */
@Slf4j
@Service
public class HBaseSensorDataService {

    @Autowired
    private Connection hbaseConnection;

    @Autowired
    private HBaseConfig hbaseConfig;

    /**
     * 扫描iot_sensor_data表中的所有数据
     *
     * @return 传感器数据列表
     */
    public List<IoTSensorData> scanAllSensorData() {
        List<IoTSensorData> sensorDataList = new ArrayList<>();
        
        try (Table table = hbaseConnection.getTable(TableName.valueOf(hbaseConfig.getTableName()))) {
            Scan scan = new Scan();
            // 设置扫描的缓存大小，提高性能
            scan.setCaching(1000);
            scan.setBatch(100);
            
            try (ResultScanner scanner = table.getScanner(scan)) {
                for (Result result : scanner) {
                    sensorDataList.addAll(parseResult(result));
                }
            }
            
            log.info("成功扫描到 {} 条传感器数据", sensorDataList.size());
            
        } catch (IOException e) {
            log.error("扫描HBase表失败", e);
            throw new RuntimeException("扫描HBase表失败", e);
        }
        
        return sensorDataList;
    }

    /**
     * 根据行键前缀扫描数据
     *
     * @param rowKeyPrefix 行键前缀
     * @return 传感器数据列表
     */
    public List<IoTSensorData> scanByRowKeyPrefix(String rowKeyPrefix) {
        List<IoTSensorData> sensorDataList = new ArrayList<>();
        
        try (Table table = hbaseConnection.getTable(TableName.valueOf(hbaseConfig.getTableName()))) {
            Scan scan = new Scan();
            scan.withStartRow(Bytes.toBytes(rowKeyPrefix));
            scan.withStopRow(Bytes.toBytes(rowKeyPrefix + "~"));
            scan.setCaching(1000);
            scan.setBatch(100);
            
            try (ResultScanner scanner = table.getScanner(scan)) {
                for (Result result : scanner) {
                    sensorDataList.addAll(parseResult(result));
                }
            }
            
            log.info("根据前缀 '{}' 扫描到 {} 条传感器数据", rowKeyPrefix, sensorDataList.size());
            
        } catch (IOException e) {
            log.error("根据前缀扫描HBase表失败", e);
            throw new RuntimeException("根据前缀扫描HBase表失败", e);
        }
        
        return sensorDataList;
    }

    /**
     * 限制扫描结果数量
     *
     * @param limit 限制数量
     * @return 传感器数据列表
     */
    public List<IoTSensorData> scanWithLimit(int limit) {
        List<IoTSensorData> sensorDataList = new ArrayList<>();
        
        try (Table table = hbaseConnection.getTable(TableName.valueOf(hbaseConfig.getTableName()))) {
            Scan scan = new Scan();
            scan.setLimit(limit);
            scan.setCaching(Math.min(limit, 1000));
            scan.setBatch(100);
            
            try (ResultScanner scanner = table.getScanner(scan)) {
                for (Result result : scanner) {
                    sensorDataList.addAll(parseResult(result));
                    if (sensorDataList.size() >= limit) {
                        break;
                    }
                }
            }
            
            log.info("限制扫描到 {} 条传感器数据", sensorDataList.size());
            
        } catch (IOException e) {
            log.error("限制扫描HBase表失败", e);
            throw new RuntimeException("限制扫描HBase表失败", e);
        }
        
        return sensorDataList;
    }

    /**
     * 解析HBase Result对象
     *
     * @param result HBase查询结果
     * @return 传感器数据列表
     */
    private List<IoTSensorData> parseResult(Result result) {
        List<IoTSensorData> dataList = new ArrayList<>();
        
        if (result.isEmpty()) {
            return dataList;
        }
        
        String rowKey = Bytes.toString(result.getRow());
        
        for (Cell cell : result.listCells()) {
            String columnFamily = Bytes.toString(CellUtil.cloneFamily(cell));
            String columnName = Bytes.toString(CellUtil.cloneQualifier(cell));
            String value = Bytes.toString(CellUtil.cloneValue(cell));
            Long timestamp = cell.getTimestamp();
            
            IoTSensorData sensorData = new IoTSensorData(rowKey, columnFamily, columnName, timestamp, value);
            
            // 尝试解析JSON中的数值
            try {
                Double sensorValue = parseValueFromJson(value, columnName);
                sensorData.setSensorValue(sensorValue);
            } catch (Exception e) {
                log.warn("解析传感器值失败，rowKey: {}, column: {}, value: {}", rowKey, columnName, value);
            }
            
            dataList.add(sensorData);
        }
        
        return dataList;
    }

    /**
     * 从JSON字符串中解析传感器值
     * 例如：{"temperature":{"value":80.2}} -> 80.2
     *
     * @param jsonValue JSON字符串
     * @param sensorType 传感器类型
     * @return 传感器数值
     */
    private Double parseValueFromJson(String jsonValue, String sensorType) {
        try {
            // 简单的JSON解析，适用于格式: {"temperature":{"value":80.2}}
            if (jsonValue.contains("\"value\":")) {
                String valueStr = jsonValue.substring(jsonValue.indexOf("\"value\":") + 8);
                valueStr = valueStr.substring(0, valueStr.indexOf("}")).trim();
                return Double.parseDouble(valueStr);
            }
        } catch (Exception e) {
            log.debug("JSON解析失败: {}", jsonValue);
        }
        return null;
    }

    /**
     * 检查表是否存在
     *
     * @return 表是否存在
     */
    public boolean isTableExists() {
        try (Admin admin = hbaseConnection.getAdmin()) {
            return admin.tableExists(TableName.valueOf(hbaseConfig.getTableName()));
        } catch (IOException e) {
            log.error("检查表是否存在失败", e);
            return false;
        }
    }
} 