package com.nexuscale.nexusscalemanage.util;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse {
    public static Map<String,Object> success(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("state", 200);
        map.put("msg", "成功");
        return map;
    }
    public static Map<String,Object> success(Object data){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("state", 200);
        map.put("msg", "成功");
        map.put("data", data);
        return map;
    }
    public static Map<String,Object> fail(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("state", 500);
        map.put("msg", "失败");
        return map;
    }
    public static Map<String,Object> fail(String msg){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("state", 500);
        map.put("msg", msg);
        return map;
    }
}
