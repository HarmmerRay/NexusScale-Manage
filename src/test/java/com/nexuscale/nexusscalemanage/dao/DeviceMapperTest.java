package com.nexuscale.nexusscalemanage.dao;

import com.nexuscale.nexusscalemanage.entity.Device;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DeviceMapperTest {
    @Autowired
    DeviceMapper deviceMapper;
    @Test
    public void insert() {
        Device device = new Device();
        device.setDeviceMac("TS-"+System.currentTimeMillis());
        device.setDeviceName("温度传感器3");
        device.setState(0);
        device.setUserId("13290824341-1744973022235");
        int res = deviceMapper.insert(device);
        System.out.println("res:"+res);
//        System.out.println(System.currentTimeMillis());
    }
}
