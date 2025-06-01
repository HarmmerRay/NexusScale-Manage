package com.nexuscale.nexusscalemanage.service;

import com.nexuscale.nexusscalemanage.dto.DeviceDataResponse;
import com.nexuscale.nexusscalemanage.entity.Device;
import com.nexuscale.nexusscalemanage.entity.DeviceTemplate;

import java.util.List;

public interface DeviceService {
    List<Device> allDevices(String userId);
    List<Device> searchDevices(String searchKey);
    List<Device> searchDevicesByUserIdAndKey(String userId, String searchKey);
    Device createDevice(Device device);
    int deleteDevice(long device_id);
    int batchDeleteDevices(List<Long> device_ids);
    Device updateDevice(Device device);
    boolean updateDeviceState(int deviceId, int state);
    DeviceTemplate getDeviceTemplateByDeviceId(int deviceId);
    DeviceDataResponse getDeviceData(Long deviceId, Integer timePeriodHours, Integer samplingIntervalMinutes);
}
