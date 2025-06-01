package com.nexuscale.nexusscalemanage;

import com.nexuscale.nexusscalemanage.entity.IoTSensorData;
import com.nexuscale.nexusscalemanage.service.HBaseSensorDataService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * HBase传感器数据测试类
 */
@Slf4j
@SpringBootTest
public class HBaseSensorDataTest {

    @Autowired
    private HBaseSensorDataService hBaseSensorDataService;

    /**
     * 测试扫描所有传感器数据
     */
    @Test
    public void testScanAllSensorData() {
        log.info("=== 开始测试扫描所有传感器数据 ===");
        
        try {
            // 检查表是否存在
            if (!hBaseSensorDataService.isTableExists()) {
                log.error("表 iot_sensor_data 不存在");
                return;
            }
            
            // 扫描所有数据
            List<IoTSensorData> sensorDataList = hBaseSensorDataService.scanAllSensorData();
            
            log.info("扫描结果总数: {}", sensorDataList.size());
            
            // 打印前10条数据作为示例
            int printCount = Math.min(10, sensorDataList.size());
            for (int i = 0; i < printCount; i++) {
                IoTSensorData data = sensorDataList.get(i);
                log.info("数据 {}: RowKey={}, 列族={}, 列名={}, 时间戳={}, 原始值={}, 解析值={}", 
                    i + 1, data.getRowKey(), data.getColumnFamily(), data.getColumnName(), 
                    data.getTimestamp(), data.getRawValue(), data.getSensorValue());
            }
            
            if (sensorDataList.size() > 10) {
                log.info("... 还有 {} 条数据未显示", sensorDataList.size() - 10);
            }
            
        } catch (Exception e) {
            log.error("测试失败", e);
        }
        
        log.info("=== 测试扫描所有传感器数据结束 ===");
    }

    /**
     * 测试限制数量扫描
     */
    @Test
    public void testScanWithLimit() {
        log.info("=== 开始测试限制数量扫描 ===");
        
        try {
            int limit = 5;
            List<IoTSensorData> sensorDataList = hBaseSensorDataService.scanWithLimit(limit);
            
            log.info("限制扫描结果数量: {}", sensorDataList.size());
            
            for (int i = 0; i < sensorDataList.size(); i++) {
                IoTSensorData data = sensorDataList.get(i);
                log.info("数据 {}: RowKey={}, 列族={}, 列名={}, 时间戳={}, 原始值={}, 解析值={}", 
                    i + 1, data.getRowKey(), data.getColumnFamily(), data.getColumnName(), 
                    data.getTimestamp(), data.getRawValue(), data.getSensorValue());
            }
            
        } catch (Exception e) {
            log.error("测试失败", e);
        }
        
        log.info("=== 测试限制数量扫描结束 ===");
    }

    /**
     * 测试根据行键前缀扫描
     */
    @Test
    public void testScanByRowKeyPrefix() {
        log.info("=== 开始测试根据行键前缀扫描 ===");
        
        try {
            // 测试前缀，可以根据实际数据调整
            String prefix = "13";
            List<IoTSensorData> sensorDataList = hBaseSensorDataService.scanByRowKeyPrefix(prefix);
            
            log.info("前缀 '{}' 扫描结果数量: {}", prefix, sensorDataList.size());
            
            // 打印前5条数据作为示例
            int printCount = Math.min(5, sensorDataList.size());
            for (int i = 0; i < printCount; i++) {
                IoTSensorData data = sensorDataList.get(i);
                log.info("数据 {}: RowKey={}, 列族={}, 列名={}, 时间戳={}, 原始值={}, 解析值={}", 
                    i + 1, data.getRowKey(), data.getColumnFamily(), data.getColumnName(), 
                    data.getTimestamp(), data.getRawValue(), data.getSensorValue());
            }
            
        } catch (Exception e) {
            log.error("测试失败", e);
        }
        
        log.info("=== 测试根据行键前缀扫描结束 ===");
    }

    /**
     * 测试表是否存在
     */
    @Test
    public void testTableExists() {
        log.info("=== 开始测试表是否存在 ===");
        
        boolean exists = hBaseSensorDataService.isTableExists();
        log.info("表 iot_sensor_data 是否存在: {}", exists);
        
        log.info("=== 测试表是否存在结束 ===");
    }

    /**
     * 综合测试方法
     */
    @Test
    public void comprehensiveTest() {
        log.info("=== 开始综合测试 ===");
        
        // 1. 检查表是否存在
        testTableExists();
        
        // 2. 限制数量扫描
        testScanWithLimit();
        
        // 3. 前缀扫描
        testScanByRowKeyPrefix();
        
        log.info("=== 综合测试结束 ===");
    }
} 