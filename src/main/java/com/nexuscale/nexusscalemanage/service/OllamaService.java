package com.nexuscale.nexusscalemanage.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OllamaService {
    
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "deepseek-r1:1.5b";
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 调用Ollama API获取回复
     */
    public String generateResponse(String prompt) {
        try {
            // 使用ObjectMapper构建JSON请求，自动处理特殊字符转义
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("model", MODEL_NAME);
            requestMap.put("prompt", prompt);
            requestMap.put("stream", false);
            
            String jsonRequest = objectMapper.writeValueAsString(requestMap);
            
            log.info("Original prompt: {}", prompt);
            log.info("Sending request to Ollama: {}", jsonRequest);
            
            // 创建HTTP连接
            URL url = new URL(OLLAMA_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000); // 10秒连接超时
            connection.setReadTimeout(120000);   // 120秒读取超时
            
            // 发送请求
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // 读取响应
            int responseCode = connection.getResponseCode();
            log.info("Ollama response code: {}", responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    
                    String jsonResponse = response.toString();
                    log.info("Ollama response: {}", jsonResponse);
                    
                    // 解析JSON响应，提取response字段
                    return extractResponseFromJson(jsonResponse);
                }
            } else {
                // 读取错误信息
                try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    log.error("Ollama error response: {}", errorResponse.toString());
                    return "调用AI模型时发生错误：" + errorResponse.toString();
                }
            }
            
        } catch (Exception e) {
            log.error("Error calling Ollama API: {}", e.getMessage(), e);
            return "调用AI模型时发生异常：" + e.getMessage();
        }
    }
    
    /**
     * 从JSON响应中提取response字段
     */
    private String extractResponseFromJson(String jsonResponse) {
        try {
            // 使用ObjectMapper解析JSON响应
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            
            // 检查是否有错误
            if (jsonNode.has("error")) {
                String error = jsonNode.get("error").asText();
                log.error("Ollama API returned error: {}", error);
                return "AI模型返回错误：" + error;
            }
            
            // 提取response字段
            if (jsonNode.has("response")) {
                String response = jsonNode.get("response").asText();
                
                // 清理特殊字符和标记
                response = cleanResponse(response);
                
                return response;
            } else {
                return "AI模型响应格式异常：缺少response字段";
            }
            
        } catch (Exception e) {
            log.error("Error parsing JSON response: {}", e.getMessage(), e);
            return "解析AI模型响应时发生错误";
        }
    }
    
    /**
     * 清理响应内容，去除特殊字符和标记
     */
    private String cleanResponse(String response) {
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