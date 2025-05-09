package com.nexuscale.nexusscalemanage.service;

import com.nexuscale.nexusscalemanage.entity.Log;

import java.util.List;

public interface LogService {
    // 分页查询日志
    List<Log> getPageLogs(int currentPage,int pageSize);
    List<Log> getPageLogsTimeDesc(int currentPage,int pageSize);
    List<Log> getPageLogsTimeDesc(int currentPage,int pageSize,String searchKeyWord);
    long getLogCount();
    long getLogCountSearch(String searchKeyWord);
    // 批量删除日志
    int batchDeleteLog(List<Log> logs);
    // 删除单个日志
    int deleteLog(Log log);
    // 批量插入日志
    // 插入单个日志
    int insertLog(Log log);
}
