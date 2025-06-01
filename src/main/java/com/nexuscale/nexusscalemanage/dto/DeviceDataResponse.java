package com.nexuscale.nexusscalemanage.dto;

import lombok.Data;

import java.util.List;

/**
 * 设备数据响应DTO
 */
@Data
public class DeviceDataResponse {
    
    /**
     * 时间数组，格式化后的时间字符串
     */
    private List<String> times;
    
    /**
     * 数值数组，对应时间点的传感器数值
     */
    private List<Double> values;
    
    public DeviceDataResponse() {}
    
    public DeviceDataResponse(List<String> times, List<Double> values) {
        this.times = times;
        this.values = values;
    }
} 