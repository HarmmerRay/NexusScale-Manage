package com.nexuscale.nexusscalemanage.service;

import com.nexuscale.nexusscalemanage.dto.DeviceStateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 向Redis list中添加设备状态消息
     * @param topic Redis list的key (使用device_template的en_name)
     * @param deviceStateMessage 设备状态消息对象
     */
    public void pushDeviceStateMessage(String topic, DeviceStateMessage deviceStateMessage) {
        try {
            // 向list右侧添加数据 (RPUSH)
            redisTemplate.opsForList().rightPush(topic, deviceStateMessage);
            System.out.println("成功向Redis topic: " + topic + " 推送消息: " + deviceStateMessage);
        } catch (Exception e) {
            System.err.println("向Redis推送消息失败: " + e.getMessage());
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 获取Redis list的长度
     * @param topic Redis list的key
     * @return list长度
     */
    public Long getListSize(String topic) {
        return redisTemplate.opsForList().size(topic);
    }

    /**
     * 从Redis list左侧弹出一个元素 (LPOP)
     * @param topic Redis list的key
     * @return 弹出的元素
     */
    public Object popDeviceStateMessage(String topic) {
        return redisTemplate.opsForList().leftPop(topic);
    }
} 