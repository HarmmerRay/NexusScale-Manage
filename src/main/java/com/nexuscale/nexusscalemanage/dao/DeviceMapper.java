package com.nexuscale.nexusscalemanage.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexuscale.nexusscalemanage.entity.Device;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
    List<Device> selectList(QueryWrapper<Object> queryWrapper);
}
