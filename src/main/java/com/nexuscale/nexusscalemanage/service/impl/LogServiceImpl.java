package com.nexuscale.nexusscalemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexuscale.nexusscalemanage.dao.LogMapper;
import com.nexuscale.nexusscalemanage.entity.Log;
import com.nexuscale.nexusscalemanage.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {
    @Autowired
    LogMapper logMapper;

    @Override
    public List<Log> getPageLogs( int currentPage,int pageSize) {
        // QueryMapper对象是用在where筛选上的
        // 分页需要@Configuration 给MybatisPlus配置上
        Page<Log> logPage = new Page<>(currentPage,pageSize);
        System.out.println(currentPage + " " + pageSize);
        return logMapper.selectPage(logPage,null).getRecords();
    }

    @Override
    public List<Log> getPageLogsTimeDesc(int currentPage, int pageSize) {
        // QueryMapper对象是用在where筛选上的
        // 分页需要@Configuration 给MybatisPlus配置上
        Page<Log> logPage = new Page<>(currentPage,pageSize);
        QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("operation_time");
//        System.out.println(currentPage + " " + pageSize);
        return logMapper.selectPage(logPage,queryWrapper).getRecords();
    }

    @Override
    public List<Log> getPageLogsTimeDesc(int currentPage,int pageSize, String searchKeyWord) {
        Page<Log> logPage = new Page<>(currentPage,pageSize);
        QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("operation_time");
        queryWrapper.like("path",searchKeyWord);
//        System.out.println(currentPage + " " + pageSize);
        return logMapper.selectPage(logPage,queryWrapper).getRecords();
    }

    @Override
    public long getLogCount() {
        return  logMapper.selectPage(new Page<Log>(1,10),null).getTotal();
    }

    @Override
    public long getLogCountSearch(String searchKeyWord) {
        Page<Log> logPage = new Page<>(1,10);
        QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("path",searchKeyWord);
        return logMapper.selectPage(logPage,queryWrapper).getTotal();
    }

    @Override
    public int batchDeleteLog(List<Log> logs) {  // 返回了删除的日志数量
        // operation_time在 logs[0].getOperationTime 和 logs[9].getOperationTime之间的所有log
        LocalDateTime startTime = logs.get(0).getOperationTime();
        LocalDateTime endTime = logs.get(logs.size()-1).getOperationTime();
        QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
        // 确保开始时间先于结束时间
        if (startTime.isAfter(endTime)) {
            LocalDateTime temp = startTime;
            startTime = endTime;
            endTime = temp;
        }
        System.out.println("startTime:"+startTime + " " + "endTime:"+ endTime);
        queryWrapper.between("operation_time",startTime,endTime);
        return logMapper.delete(queryWrapper);
//        return 0;
    }

    @Override
    public int deleteLog(Log log) {
        QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("operation_time",log.getOperationTime());
        logMapper.delete(queryWrapper);
        return 0;
    }

    @Override
    public int insertLog(Log log) {
        return logMapper.insert(log);
    }

}
