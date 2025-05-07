package com.nexuscale.nexusscalemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nexuscale.nexusscalemanage.dao.DeviceMapper;
import com.nexuscale.nexusscalemanage.entity.Device;
import com.nexuscale.nexusscalemanage.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceMapper deviceMapper;

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
    public Device createDevice(Device device) {
        if (deviceMapper.insert(device) == 1){
            return device;
        }
        return null;
    }

    @Override
    public Device deleteDevice(Device device) {
        if (deviceMapper.deleteById(device.getDeviceId()) == 1){
            return device;
        }
        return null;
    }

    @Override
    public Device updateDevice(Device device) {
        if (deviceMapper.updateById(device) == 1){
            return device;
        }
        return null;
    }
}
