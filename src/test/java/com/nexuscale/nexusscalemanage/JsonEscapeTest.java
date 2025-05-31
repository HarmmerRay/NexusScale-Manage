package com.nexuscale.nexusscalemanage;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试JSON转义功能
 */
public class JsonEscapeTest {
    
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 测试包含特殊字符的中文输入
        String[] testInputs = {
            "你好，世界！",
            "这是一个\n换行测试",
            "包含\"引号\"的文本",
            "包含\t制表符和\r回车的文本",
            "中文标点：，。！？；：",
            "混合文本：Hello 你好\nNew line \"quote\" test"
        };
        
        System.out.println("=== JSON转义测试 ===");
        
        for (String input : testInputs) {
            try {
                // 构建请求Map
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("model", "deepseek-r1:1.5b");
                requestMap.put("prompt", input);
                requestMap.put("stream", false);
                
                // 转换为JSON字符串
                String jsonRequest = objectMapper.writeValueAsString(requestMap);
                
                System.out.println("输入: " + input.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t"));
                System.out.println("JSON: " + jsonRequest);
                System.out.println("---");
                
            } catch (Exception e) {
                System.out.println("错误处理输入: " + input);
                System.out.println("错误: " + e.getMessage());
                System.out.println("---");
            }
        }
        
        System.out.println("测试完成！");
    }
} 