package com.nexuscale.nexusscalemanage.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexuscale.nexusscalemanage.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
} 