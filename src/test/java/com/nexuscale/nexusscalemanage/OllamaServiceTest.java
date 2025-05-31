package com.nexuscale.nexusscalemanage;

/**
 * 简单的测试类，用于验证响应清理功能
 */
public class OllamaServiceTest {
    
    public static void main(String[] args) {
        // 模拟包含特殊字符的响应
        String testResponse = "\\u003cthink\\u003e这是思考内容\\u003c/think\\u003e您好！请问有什么可以帮助您的？";
        
        // 测试清理功能
        String cleaned = cleanResponse(testResponse);
        System.out.println("原始响应: " + testResponse);
        System.out.println("清理后响应: " + cleaned);
    }
    
    /**
     * 清理响应内容，去除特殊字符和标记
     */
    private static String cleanResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return "AI助手暂时无法回复，请稍后重试。";
        }
        
        // 去除特殊的Unicode字符和XML/HTML标记
        response = response.replaceAll("\\\\u[0-9a-fA-F]{4}", ""); // 去除Unicode转义
        response = response.replaceAll("<[^>]*>", ""); // 去除HTML/XML标签
        response = response.replaceAll("\\\\[a-zA-Z]+", ""); // 去除反斜杠转义序列
        
        // 去除特定的思考标记
        response = response.replaceAll("(?i)<think>.*?</think>", "");
        response = response.replaceAll("(?i)\\\\u003cthink\\\\u003e.*?\\\\u003c/think\\\\u003e", "");
        
        // 去除多余的空白字符
        response = response.replaceAll("\\s+", " ").trim();
        
        // 如果清理后内容为空或太短，返回默认回复
        if (response.length() < 2) {
            return "您好！我是AI助手，很高兴为您服务。请问有什么可以帮助您的吗？";
        }
        
        return response;
    }
} 