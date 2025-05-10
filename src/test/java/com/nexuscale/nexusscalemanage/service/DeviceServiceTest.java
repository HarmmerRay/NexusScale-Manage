package com.nexuscale.nexusscalemanage.service;

import com.nexuscale.nexusscalemanage.entity.Device;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class DeviceServiceTest {
    @Autowired
    DeviceService deviceService;

    @Test
    public void batchDelete() {
        List<Long> deviceIds = new ArrayList<>();
        deviceIds.add(1L);
        deviceIds.add(2L);
        deviceIds.add(3L);
        int nums = deviceService.batchDeleteDevices(deviceIds);
        System.out.println(nums);
    }

    @Test
    public void searchDevices() {
        List<Device> deviceList = deviceService.searchDevices("温度");
        for (Device device : deviceList) {
            System.out.println(device);
        }
    }
}
