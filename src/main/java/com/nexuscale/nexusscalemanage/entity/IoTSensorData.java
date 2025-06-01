package com.nexuscale.nexusscalemanage.entity;

import lombok.Data;

/**
 * IoT传感器数据实体类
 * 用于存储从HBase iot_sensor_data表查询的数据
 */
@Data
public class IoTSensorData {
    
    /**
     * 行键 (rowkey)
     * 格式示例：13_1748753523972
     */
    private String rowKey;
    
    /**
     * 列族
     * 通常是：cf1
     */
    private String columnFamily;
    
    /**
     * 列名 (传感器类型)
     * 例如：temperature, humidity, pressure等
     */
    private String columnName;
    
    /**
     * HBase时间戳
     */
    private Long timestamp;
    
    /**
     * 原始JSON值
     * 例如：{"temperature":{"value":80.2}}
     */
    private String rawValue;
    
    /**
     * 解析后的传感器值
     */
    private Double sensorValue;
    
    /**
     * 传感器类型（从列名解析）
     */
    private String sensorType;
    
    public IoTSensorData() {}
    
    public IoTSensorData(String rowKey, String columnFamily, String columnName, Long timestamp, String rawValue) {
        this.rowKey = rowKey;
        this.columnFamily = columnFamily;
        this.columnName = columnName;
        this.timestamp = timestamp;
        this.rawValue = rawValue;
        this.sensorType = columnName;
    }
} 