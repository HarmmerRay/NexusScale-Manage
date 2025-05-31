package com.nexuscale.nexusscalemanage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexuscale.nexusscalemanage.dao.MessageMapper;
import com.nexuscale.nexusscalemanage.entity.Message;
import com.nexuscale.nexusscalemanage.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Override
    public int saveMessage(Message message) {
        return messageMapper.insert(message);
    }
} 