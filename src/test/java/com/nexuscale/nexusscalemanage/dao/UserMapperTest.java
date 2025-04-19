package com.nexuscale.nexusscalemanage.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nexuscale.nexusscalemanage.entity.User;
import com.nexuscale.nexusscalemanage.util.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void insertUser() {
        User user = new User();
//        user.setUserId(UserId.getUserId());
//        user.setPhoneNumber("13290824341");
//        user.setUserName("致远科技");
//        System.out.println(user);
//        System.out.println(userMapper.insert(user));;
    }
    @Test
    public void selectUser() {
        User user = new User();
        user.setPhoneNumber("13290824341");
        QueryWrapper<User> ew = new QueryWrapper<>();
        ew.eq("phone_number", user.getPhoneNumber());
        List<User> list = userMapper.selectList(ew);
        System.out.println(list.get(0).toString());
    }
}
