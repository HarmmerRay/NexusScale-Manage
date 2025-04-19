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
        device.setDeviceName("test");
        deviceMapper.insert(device);
    }
}
