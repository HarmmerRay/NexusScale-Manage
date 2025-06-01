package com.nexuscale.nexusscalemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nexuscale.nexusscalemanage.dao.DeviceMapper;
import com.nexuscale.nexusscalemanage.dao.DeviceTemplateMapper;
import com.nexuscale.nexusscalemanage.dto.DeviceDataResponse;
import com.nexuscale.nexusscalemanage.entity.Device;
import com.nexuscale.nexusscalemanage.entity.DeviceTemplate;
import com.nexuscale.nexusscalemanage.entity.IoTSensorData;
import com.nexuscale.nexusscalemanage.service.DeviceService;
import com.nexuscale.nexusscalemanage.service.HBaseSensorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceTemplateMapper deviceTemplateMapper;
    @Autowired
    private HBaseSensorDataService hBaseSensorDataService;

    @Override
    public List<Device> allDevices(String userId) {
        QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<Device> list = deviceMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            return list;
        }
        return null;
    }

    @Override
    public List<Device> searchDevices(String searchKey) {
        // searchKey 是user_id或device_name，以此来调用deviceMapper的功能实现devices数据的查找
        String keyword = searchKey.trim();

        // 使用 LambdaQueryWrapper 构建动态查询条件
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();

        // 尝试匹配 user_id（精确查询）或 device_name（模糊查询）
        wrapper
                .eq(Device::getUserId, keyword) // 处理 user_id 精确匹配
                .or() // 或者
                .like(Device::getDeviceName, keyword); // 处理 device_name 模糊匹配

        return deviceMapper.selectList(wrapper);
    }

    @Override
    public List<Device> searchDevicesByUserIdAndKey(String userId, String searchKey) {
        // 先根据user_id筛选，再根据searchKey搜索设备名称、MAC地址等
        String keyword = searchKey.trim();

        // 使用 LambdaQueryWrapper 构建查询条件
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();

        // 必须匹配指定的user_id
        wrapper.eq(Device::getUserId, userId);

        // 如果有搜索关键字，则进行模糊查询
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                    .like(Device::getDeviceName, keyword) // 设备名称模糊匹配
                    .or()
                    .like(Device::getDeviceMac, keyword) // 设备MAC地址模糊匹配
            );
        }

        return deviceMapper.selectList(wrapper);
    }

    @Override
    public Device createDevice(Device device) {
        if (deviceMapper.insert(device) == 1){
            return device;
        }
        return null;
    }

    @Override
    public int deleteDevice(long device_id) {
        return deviceMapper.deleteById(device_id);
    }

    @Override
    public int batchDeleteDevices(List<Long> device_ids) {
        // 批量删除这一批device_ids对应的各个设备记录。
        if (device_ids == null || device_ids.isEmpty()) {
            return 0; // 避免无效操作
        }

        // 使用MyBatis-Plus的delete方法，通过ID集合批量删除
        return deviceMapper.delete(Wrappers.<Device>lambdaQuery()
                .in(Device::getDeviceId, device_ids));
    }

    @Override
    public Device updateDevice(Device device) {
        if (deviceMapper.updateById(device) == 1){
            return device;
        }
        return null;
    }

    @Override
    public boolean updateDeviceState(int deviceId, int state) {
        // 构建更新条件，只更新指定设备的状态
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getDeviceId, deviceId);
        
        // 创建要更新的设备对象，只设置状态字段
        Device device = new Device();
        device.setState(state);
        
        // 执行更新操作
        int result = deviceMapper.update(device, wrapper);
        return result > 0;
    }

    @Override
    public DeviceTemplate getDeviceTemplateByDeviceId(int deviceId) {
        // 先查询设备信息获取dt_id
        Device device = deviceMapper.selectById(deviceId);
        if (device == null || device.getDtId() == null) {
            return null;
        }
        
        // 根据dt_id查询设备模板
        return deviceTemplateMapper.selectById(device.getDtId());
    }

    @Override
    public DeviceDataResponse getDeviceData(Long deviceId, Integer timePeriodHours, Integer samplingIntervalMinutes) {
        try {
            // 1. 根据设备ID构造行键前缀进行查询
            String rowKeyPrefix = deviceId.toString();
            List<IoTSensorData> sensorDataList = hBaseSensorDataService.scanByRowKeyPrefix(rowKeyPrefix);
            
            if (sensorDataList == null || sensorDataList.isEmpty()) {
                return new DeviceDataResponse(new ArrayList<>(), new ArrayList<>());
            }
            
            // 2. 计算时间范围
            long currentTime = System.currentTimeMillis();
            long timeRangeMs = timePeriodHours * 60 * 60 * 1000L; // 转换为毫秒
            long startTime = currentTime - timeRangeMs;
            
            // 3. 过滤时间范围内的数据
            List<IoTSensorData> filteredData = sensorDataList.stream()
                    .filter(data -> data.getTimestamp() != null && data.getTimestamp() >= startTime)
                    .filter(data -> data.getSensorValue() != null)
                    .sorted((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()))
                    .collect(Collectors.toList());
            
            if (filteredData.isEmpty()) {
                return new DeviceDataResponse(new ArrayList<>(), new ArrayList<>());
            }
            
            // 4. 根据采样间隔对数据进行采样
            List<IoTSensorData> sampledData = sampleData(filteredData, samplingIntervalMinutes);
            
            // 5. 格式化时间和值
            List<String> times = new ArrayList<>();
            List<Double> values = new ArrayList<>();
            
            for (IoTSensorData data : sampledData) {
                String timeStr = formatTime(data.getTimestamp(), timePeriodHours);
                times.add(timeStr);
                values.add(data.getSensorValue());
            }
            
            return new DeviceDataResponse(times, values);
            
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("获取设备数据失败: " + e.getMessage());
            return new DeviceDataResponse(new ArrayList<>(), new ArrayList<>());
        }
    }
    
    /**
     * 根据采样间隔对数据进行采样
     */
    private List<IoTSensorData> sampleData(List<IoTSensorData> data, Integer samplingIntervalMinutes) {
        if (samplingIntervalMinutes == null || samplingIntervalMinutes <= 0) {
            return data; // 不采样，返回所有数据
        }
        
        List<IoTSensorData> sampledData = new ArrayList<>();
        long intervalMs = samplingIntervalMinutes * 60 * 1000L; // 转换为毫秒
        
        if (!data.isEmpty()) {
            sampledData.add(data.get(0)); // 添加第一个数据点
            long lastTimestamp = data.get(0).getTimestamp();
            
            for (IoTSensorData sensorData : data) {
                if (sensorData.getTimestamp() - lastTimestamp >= intervalMs) {
                    sampledData.add(sensorData);
                    lastTimestamp = sensorData.getTimestamp();
                }
            }
        }
        
        return sampledData;
    }
    
    /**
     * 根据时间范围格式化时间戳
     */
    private String formatTime(Long timestamp, Integer timePeriodHours) {
        LocalDateTime time = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp), 
            ZoneId.systemDefault()
        );
        
        String timeStr;
        if (timePeriodHours <= 12) {
            // 小时内显示：HH:mm
            timeStr = time.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            // 天级别显示：MM-dd HH:mm
            timeStr = time.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        }
        
        return timeStr;
    }
}
