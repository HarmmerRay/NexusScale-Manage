package com.nexuscale.nexusscalemanage;

import com.nexuscale.nexusscalemanage.dto.DeviceDataResponse;
import com.nexuscale.nexusscalemanage.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 设备数据接口集成测试
 */
@Slf4j
@SpringBootTest
public class DeviceDataIntegrationTest {

    @Autowired
    private DeviceService deviceService;

    /**
     * 测试获取设备数据接口
     */
    @Test
    public void testGetDeviceData() {
        log.info("=== 开始测试获取设备数据接口 ===");
        
        try {
            // 测试参数
            Long deviceId = 13L;  // 根据您的数据样例，使用设备ID 13
            Integer timePeriodHours = 24;  // 24小时内的数据
            Integer samplingIntervalMinutes = 30;  // 30分钟采样间隔
            
            log.info("测试参数 - 设备ID: {}, 时间范围: {}小时, 采样间隔: {}分钟", 
                    deviceId, timePeriodHours, samplingIntervalMinutes);
            
            // 调用服务方法
            DeviceDataResponse response = deviceService.getDeviceData(deviceId, timePeriodHours, samplingIntervalMinutes);
            
            if (response != null) {
                log.info("获取设备数据成功");
                log.info("时间数据点数量: {}", response.getTimes().size());
                log.info("数值数据点数量: {}", response.getValues().size());
                
                // 打印前5个数据点作为示例
                int printCount = Math.min(5, response.getTimes().size());
                for (int i = 0; i < printCount; i++) {
                    log.info("数据点 {}: 时间={}, 值={}", 
                            i + 1, response.getTimes().get(i), response.getValues().get(i));
                }
                
                if (response.getTimes().size() > 5) {
                    log.info("... 还有 {} 个数据点未显示", response.getTimes().size() - 5);
                }
            } else {
                log.error("获取设备数据失败，返回null");
            }
            
        } catch (Exception e) {
            log.error("测试失败", e);
        }
        
        log.info("=== 测试获取设备数据接口结束 ===");
    }

    /**
     * 测试不同时间范围的数据格式
     */
    @Test
    public void testDifferentTimeRanges() {
        log.info("=== 开始测试不同时间范围的数据格式 ===");
        
        Long deviceId = 13L;
        
        // 测试小于等于12小时的情况（应该显示 HH:mm 格式）
        testTimeRange(deviceId, 6, 10, "6小时范围");
        
        // 测试大于12小时的情况（应该显示 MM-dd HH:mm 格式）
        testTimeRange(deviceId, 48, 60, "48小时范围");
        
        log.info("=== 测试不同时间范围的数据格式结束 ===");
    }
    
    private void testTimeRange(Long deviceId, Integer hours, Integer samplingMinutes, String description) {
        try {
            log.info("测试 {} - {}小时", description, hours);
            
            DeviceDataResponse response = deviceService.getDeviceData(deviceId, hours, samplingMinutes);
            
            if (response != null && !response.getTimes().isEmpty()) {
                log.info("{} - 获取到 {} 个数据点", description, response.getTimes().size());
                log.info("{} - 时间格式示例: {}", description, response.getTimes().get(0));
            } else {
                log.info("{} - 未获取到数据", description);
            }
            
        } catch (Exception e) {
            log.error("{} 测试失败", description, e);
        }
    }
} 