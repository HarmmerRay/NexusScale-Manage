package com.nexuscale.nexusscalemanage.util;

import lombok.Data;

/**
 * 客服服务响应封装类
 */
@Data
public class CustomerServiceResponse {
    private String response;
    private Long timestamp;
    private String model;
    private Boolean success;
    private String error;
    
    public static CustomerServiceResponse success(String response) {
        CustomerServiceResponse result = new CustomerServiceResponse();
        result.setResponse(response);
        result.setTimestamp(System.currentTimeMillis());
        result.setModel("deepseek-r1:1.5b");
        result.setSuccess(true);
        return result;
    }
    
    public static CustomerServiceResponse error(String error) {
        CustomerServiceResponse result = new CustomerServiceResponse();
        result.setError(error);
        result.setTimestamp(System.currentTimeMillis());
        result.setSuccess(false);
        return result;
    }
} 