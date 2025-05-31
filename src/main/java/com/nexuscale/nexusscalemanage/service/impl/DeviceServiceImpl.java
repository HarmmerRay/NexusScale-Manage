package com.nexuscale.nexusscalemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
    public List<Device> searchDevices(String searchKey) {
        // searchKey 是user_id或device_name，以此来调用deviceMapper的功能实现devices数据的查找
        String keyword = searchKey.trim();

        // 使用 LambdaQueryWrapper 构建动态查询条件
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();

        // 尝试匹配 user_id（精确查询）或 device_name（模糊查询）
        wrapper
                .eq(Device::getUserId, keyword) // 处理 user_id 精确匹配
                .or() // 或者
                .like(Device::getDeviceName, keyword); // 处理 device_name 模糊匹配

        return deviceMapper.selectList(wrapper);
    }

    @Override
    public List<Device> searchDevicesByUserIdAndKey(String userId, String searchKey) {
        // 先根据user_id筛选，再根据searchKey搜索设备名称、MAC地址等
        String keyword = searchKey.trim();

        // 使用 LambdaQueryWrapper 构建查询条件
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();

        // 必须匹配指定的user_id
        wrapper.eq(Device::getUserId, userId);

        // 如果有搜索关键字，则进行模糊查询
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                    .like(Device::getDeviceName, keyword) // 设备名称模糊匹配
                    .or()
                    .like(Device::getDeviceMac, keyword) // 设备MAC地址模糊匹配
            );
        }

        return deviceMapper.selectList(wrapper);
    }

    @Override
    public Device createDevice(Device device) {
        if (deviceMapper.insert(device) == 1){
            return device;
        }
        return null;
    }

    @Override
    public int deleteDevice(long device_id) {
        return deviceMapper.deleteById(device_id);
    }

    @Override
    public int batchDeleteDevices(List<Long> device_ids) {
        // 批量删除这一批device_ids对应的各个设备记录。
        if (device_ids == null || device_ids.isEmpty()) {
            return 0; // 避免无效操作
        }

        // 使用MyBatis-Plus的delete方法，通过ID集合批量删除
        return deviceMapper.delete(Wrappers.<Device>lambdaQuery()
                .in(Device::getDeviceId, device_ids));
    }

    @Override
    public Device updateDevice(Device device) {
        if (deviceMapper.updateById(device) == 1){
            return device;
        }
        return null;
    }
}
