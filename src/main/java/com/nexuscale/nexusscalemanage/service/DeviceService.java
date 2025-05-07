package com.nexuscale.nexusscalemanage.service;

import com.nexuscale.nexusscalemanage.entity.Device;

import java.util.List;

public interface DeviceService {
    List<Device> allDevices(String userId);
    // 创建 删除 修改
    Device createDevice(Device device);
    Device deleteDevice(Device device);
    Device updateDevice(Device device);
}
