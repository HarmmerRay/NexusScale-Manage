package com.nexuscale.nexusscalemanage.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexuscale.nexusscalemanage.entity.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LogMapperTest {
    @Autowired
    LogMapper logMapper;

    @Test
    public void pageTest(){
        // QueryMapper对象是用在where筛选上的
        // 分页需要@Configuration 给MybatisPlus配置上
        Page<Log> logPage = new Page<>(1,10);
        QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("operation_time");
        System.out.println(1 + " " + 10);
        System.out.println(logMapper.selectPage(logPage,queryWrapper).getRecords());
    }
}
