package com.nexuscale.nexusscalemanage.service;


import com.nexuscale.nexusscalemanage.entity.Log;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@SpringBootTest
public class LogServiceTest {
    @Autowired
    LogService logService;
    @Test
    public void test() {
//        logService.batchDeleteLog();
        Log log = new Log();
        log.setOperationTime(LocalDateTime.of(2025, 5, 7, 23, 26, 20));
        System.out.println(logService.deleteLog(log));
    }

    @Test
    public void test2() {
        Log log1 = new Log();
        log1.setOperationTime(LocalDateTime.of(2025, 5, 3, 0, 0, 0));
        Log log2 = new Log();
        log2.setOperationTime(LocalDateTime.of(2025, 5, 4, 23, 0, 0));
        List<Log> logs = new LinkedList<>();
        logs.add(log1);
        logs.add(log2);
        System.out.println(logService.batchDeleteLog(logs));
    }
}
