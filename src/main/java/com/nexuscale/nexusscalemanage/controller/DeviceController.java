package com.nexuscale.nexusscalemanage.controller;

import com.nexuscale.nexusscalemanage.entity.Device;
import com.nexuscale.nexusscalemanage.service.DeviceService;
import com.nexuscale.nexusscalemanage.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/device")
public class DeviceController {
    @Autowired
    DeviceService deviceService;

    @PostMapping("/change_sensor_state")
    public Map<String, String> changeSensorState(@RequestParam boolean state, @RequestParam int sensor_id) {
        System.out.println("state: " + state);
        System.out.println("sensor_id: " + sensor_id);
        Map<String, String> dict = new HashMap<>();
        state = !state;
        if (state) {
            dict.put("msg", "设备状态修改成功，当前状态开启");
        } else {
            dict.put("msg", "设备状态修改成功，当前状态关闭" );
        }

        return dict;
    }
    @GetMapping("/all_devices")
    public Map<String, Object> allDevices(@RequestParam String user_id) {
//        System.out.println("user_id: " + user_id);
        List<Device> devices =  deviceService.allDevices(user_id);
//        System.out.println("devices: " + devices);
        return ApiResponse.success(devices);
    }

    @PostMapping("/create_device")
    public Map<String,Object> createDevice(@RequestBody Device device) {
        // 设备种类：温度、湿度、空气质量、土壤氮磷钾、PH值、微量元素、风速、风向、光照强度、
        return ApiResponse.success(deviceService.createDevice(device));
    }
    @PostMapping("/delete_device")
    public Map<String,Object> deleteDevice(@RequestBody long device_id) {
        return ApiResponse.success(deviceService.deleteDevice(device_id));
    }
    @PostMapping("/batch_delete_devices")
    public Map<String,Object> batchDeleteDevice(@RequestBody List<Long> device_ids) {
        return ApiResponse.success(deviceService.batchDeleteDevices(device_ids));
    }
    @GetMapping("/search_device")
    public Map<String,Object> searchDevices(@RequestParam String searchKey) {
        if (searchKey == null || searchKey.trim().isEmpty()) {
            return ApiResponse.fail("没有searchKey"); // 无搜索条件时返回所有设备（根据需求决定）
        }
        return ApiResponse.success(deviceService.searchDevices(searchKey));
    }
}
