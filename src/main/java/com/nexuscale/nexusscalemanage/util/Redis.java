package com.nexuscale.nexusscalemanage.util;

import redis.clients.jedis.Jedis;

public class Redis {
    public static boolean setValue(String key, String value) {
        Jedis jedis = new Jedis("localhost", 6379);
        try{
            String res = jedis.set(key, value);
            if (res == null){
                return false;
            }else{
                return true;
            }
        } catch (Exception e){
            System.err.println("发生错误: " + e.getMessage());
        } finally {
            // 关闭连接
            jedis.close();
        }
        return false;
    }
    public static boolean setValue(String key, String value, int seconds) {
        Jedis jedis = new Jedis("localhost", 6379);
        try{
            String res = jedis.set(key, value);
            jedis.expire(key, seconds);
            if (res == null){
                return false;
            }else{
                return true;
            }
        } catch (Exception e){
            System.err.println("发生错误: " + e.getMessage());
        } finally {
            // 关闭连接
            jedis.close();
        }
        return false;
    }
    public static String getValue(String key) {
        Jedis jedis = new Jedis("localhost", 6379);
        try{
            String value = jedis.get(key);
            if (value == null){
                return "";
            }else{
                return value;
            }
        } catch (Exception e){
            System.err.println("发生错误: " + e.getMessage());
        } finally {
            // 关闭连接
            jedis.close();
        }
        return "";
    }

    public static void main(String[] args) {
        setValue("key", "value", 60);
        System.out.println(getValue("key"));
        setValue("key", "value2", 60);
        System.out.println(getValue("key"));
    }
}
