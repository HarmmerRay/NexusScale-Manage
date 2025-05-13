package com.nexuscale.nexusscalemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexuscale.nexusscalemanage.dao.UserMapper;
import com.nexuscale.nexusscalemanage.entity.Device;
import com.nexuscale.nexusscalemanage.entity.Log;
import com.nexuscale.nexusscalemanage.entity.User;
import com.nexuscale.nexusscalemanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
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

    @Override
    public int deleteUser(String userId) {
        return userMapper.deleteById(userId);
    }

    @Override
    public int batchDeleteUser(List<String> userIds) {
        if (userIds != null && !userIds.isEmpty()) {
            return userMapper.delete(Wrappers.<User>lambdaQuery()
                    .in(User::getUserId, userIds));
        }else{
            return 0;
        }

    }

    @Override
    public int updateUser(User user) {
        return userMapper.updateById(user);
    }

    @Override
    public List<User> searchUser(int currentPage, int pageSize, String keyword) {
        String searchKey = keyword.trim();

        // 构建分页参数
        Page<User> page = new Page<>(currentPage, pageSize);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        List<User> res = new LinkedList<>();
        if (searchKey.length() == 1) {
            if (searchKey.equals("1") || searchKey.equals("0")) {
                // 按等级查询并分页
                return userMapper.selectPage(page, Wrappers.<User>lambdaQuery()
                        .eq(User::getLevel, Integer.valueOf(keyword))).getRecords();
            }
        }

        // 多条件搜索并分页
        wrapper
                .like(User::getPhoneNumber, keyword)
                .or()
                .like(User::getUserName, keyword);
        res = userMapper.selectPage(page, wrapper).getRecords();

        return res;
    }

    @Override
    public long getUserCount(String keyword) {
        String searchKey = keyword.trim();
        Page<User> page = new Page<>(1,10);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (searchKey.equals("1") || searchKey.equals("0")){
            queryWrapper.eq("level",Integer.valueOf(searchKey));
        }
        else{
            queryWrapper.like("user_name",keyword);
            queryWrapper.or().like("phone_number",keyword);
        }
        return userMapper.selectPage(page,queryWrapper).getTotal();
    }
}
