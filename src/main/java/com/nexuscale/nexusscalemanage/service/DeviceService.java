package com.nexuscale.nexusscalemanage.service;

import com.nexuscale.nexusscalemanage.entity.Device;

import java.util.List;

public interface DeviceService {
    List<Device> allDevices(String userId);
    List<Device> searchDevices(String searchKey);
    Device createDevice(Device device);
    int deleteDevice(long device_id);
    int batchDeleteDevices(List<Long> device_ids);
    Device updateDevice(Device device);
}
