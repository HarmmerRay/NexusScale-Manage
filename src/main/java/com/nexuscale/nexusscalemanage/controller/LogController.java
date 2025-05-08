package com.nexuscale.nexusscalemanage.controller;

import com.nexuscale.nexusscalemanage.entity.Log;
import com.nexuscale.nexusscalemanage.service.LogService;
import com.nexuscale.nexusscalemanage.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/log")
public class LogController {
    @Autowired
    private LogService logService;

    @GetMapping("/load_data")
    public Map<String, Object> loadData(@RequestParam int currentPage,@RequestParam int pageSize,@RequestParam String searchKeyWord) {
        // 此处分页查询日志数据有bug ，日志数据一直在不断插入更新，第一页查出来的内容可能在第二页查询时重复出现。 但毕业设计演示数据则足矣。
        // 根据searchKeyWord 查询某一页 pageSize条数据
        if (searchKeyWord == null || searchKeyWord.isEmpty()) {
            List<Log> resLog = logService.getPageLogsTimeDesc(currentPage,pageSize);
            List<Map<String,Object>> list = new LinkedList<>();
            for (Log log : resLog) {
                list.add(log.toDict());
            }
//            System.out.println(list);
            Map<String, Object> map = ApiResponse.success(list);
            map.put("currentPage", currentPage);
            map.put("total", logService.getLogCount());
            map.put("pageSize", pageSize);
            return map;
        }else{
            return ApiResponse.success(searchKeyWord);
        }
    }
    @PostMapping("/deleteLog")
    public Map<String, Object> deleteLog(@RequestParam("dateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime localDateTime) {
        // 前端使用yyyy-MM-dd HH:mm:ss  {"dateTime":""}字符串即可
        System.out.println(localDateTime);
        Log log = new Log();
        log.setOperationTime(localDateTime);
        System.out.println(logService.deleteLog(log));
        return ApiResponse.success();
    }
    @PostMapping("/batchDeleteLog")
    public Map<String, Object> batchDeleteLog(@RequestParam("dateTimeList") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  List<LocalDateTime> localDateTimeList) {
        // 前端拼接dateTime列表来传输，[] 其中每个元素是 时间字符串
        return ApiResponse.success(localDateTimeList);
    }
}
