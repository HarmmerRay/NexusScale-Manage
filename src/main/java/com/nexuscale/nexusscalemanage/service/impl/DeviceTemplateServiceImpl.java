package com.nexuscale.nexusscalemanage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexuscale.nexusscalemanage.dao.DeviceTemplateMapper;
import com.nexuscale.nexusscalemanage.entity.DeviceTemplate;
import com.nexuscale.nexusscalemanage.service.DeviceTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceTemplateServiceImpl extends ServiceImpl<DeviceTemplateMapper, DeviceTemplate> implements DeviceTemplateService {
    @Autowired
    private DeviceTemplateMapper deviceTemplateMapper;

    @Override
    public List<DeviceTemplate> getAllDeviceTemplates() {
        return deviceTemplateMapper.selectList(null);
    }
} 