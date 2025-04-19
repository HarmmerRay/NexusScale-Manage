package com.nexuscale.nexusscalemanage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/device")
public class DeviceController {
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
}
