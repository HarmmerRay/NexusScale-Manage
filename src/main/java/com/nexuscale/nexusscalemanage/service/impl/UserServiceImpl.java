package com.nexuscale.nexusscalemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexuscale.nexusscalemanage.dao.UserMapper;
import com.nexuscale.nexusscalemanage.entity.User;
import com.nexuscale.nexusscalemanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User registerUser(User user) {
        // 电话查询该用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone_number", user.getPhoneNumber());
        List<User> list = userMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        if (userMapper.insert(user) == 1) {
            return userMapper.selectList(queryWrapper).get(0);
        }else{
            return null;
        }
    }

}
