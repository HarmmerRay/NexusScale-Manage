package com.nexuscale.nexusscalemanage.controller;

import com.nexuscale.nexusscalemanage.entity.Device;
import com.nexuscale.nexusscalemanage.entity.DeviceTemplate;
import com.nexuscale.nexusscalemanage.service.DeviceService;
import com.nexuscale.nexusscalemanage.service.DeviceTemplateService;
import com.nexuscale.nexusscalemanage.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
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
    @Autowired
    DeviceTemplateService deviceTemplateService;

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
    public Map<String,Object> createDevice(HttpServletRequest request, @RequestBody Device device) {
        device.setUserId(request.getSession().getAttribute("userId").toString());
//        System.out.println("device: " + device);
        // 设备种类：温度、湿度、空气质量、土壤氮磷钾、PH值、微量元素、风速、风向、光照强度、
        return ApiResponse.success(deviceService.createDevice(device));
    }
    @PostMapping("/delete_device")
    public Map<String,Object> deleteDevice(@RequestParam long deviceId) {

        return ApiResponse.success(deviceService.deleteDevice(deviceId));
    }
    @PostMapping("/batch_delete_devices")
    public Map<String,Object> batchDeleteDevice(@RequestBody List<Long> deviceIds) {
        System.out.println("deviceIds: " + deviceIds);
        return ApiResponse.success(deviceService.batchDeleteDevices(deviceIds));
    }
    @GetMapping("/search_device")
    public Map<String,Object> searchDevices(@RequestParam String searchKey) {
        if (searchKey == null || searchKey.trim().isEmpty()) {
            return ApiResponse.fail("没有searchKey"); // 无搜索条件时返回所有设备（根据需求决定）
        }
        return ApiResponse.success(deviceService.searchDevices(searchKey));
    }
    
    @GetMapping("/search_devices_by_userid")
    public Map<String,Object> searchDevicesByUserId(@RequestParam String userId, @RequestParam(required = false) String searchKey) {
        if (userId == null || userId.trim().isEmpty()) {
            return ApiResponse.fail("用户ID不能为空");
        }
        
        // 如果没有提供searchKey，返回该用户的所有设备
        if (searchKey == null || searchKey.trim().isEmpty()) {
            return ApiResponse.success(deviceService.allDevices(userId));
        }
        
        // 根据用户ID和关键字搜索设备
        List<Device> devices = deviceService.searchDevicesByUserIdAndKey(userId, searchKey);
        return ApiResponse.success(devices);
    }
    
    @GetMapping("/search_device_templates")
    public Map<String,Object> searchDeviceTemplates() {
        List<DeviceTemplate> templates = deviceTemplateService.getAllDeviceTemplates();
        return ApiResponse.success(templates);
    }
    
    @PostMapping("/update_device_name")
    public Map<String,Object> updateDeviceName(@RequestParam long deviceId,@RequestParam String deviceName, HttpServletRequest request) {
        Device dev = new Device();
        dev.setDeviceId(deviceId);
        dev.setDeviceName(deviceName);
        return ApiResponse.success(deviceService.updateDevice(dev));
    }
}
