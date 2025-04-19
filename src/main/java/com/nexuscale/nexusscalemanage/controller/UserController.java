package com.nexuscale.nexusscalemanage.controller;

import com.nexuscale.nexusscalemanage.dao.UserMapper;
import com.nexuscale.nexusscalemanage.service.UserService;
import com.nexuscale.nexusscalemanage.util.*;
import com.nexuscale.nexusscalemanage.entity.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    //CRUD
    @PostMapping("/register")
    public Map<String,Object> register(@RequestParam String phone, @RequestParam String code, HttpServletResponse response) {
        if (PhoneRegexCheck.isValidPhoneNumber(phone)){
            if (!CodeCheck.checkCode(phone,code)){
                return ApiResponse.fail("验证码不正确");
            }
            User user = new User();
            user.setUserId(UserId.getUserId(phone));
            user.setPhoneNumber(phone);
            user.setUserName(phone.substring(7,11));

            if (userService.registerUser(user)){

                // 生成 token
                String token = EncryptionDecryption.encrypt(user.getPhoneNumber(),604800);
                // 创建一个 cookie 并将 token 放入其中
                Cookie tokenCookie = new Cookie("token", token);
                tokenCookie.setPath("/");
                tokenCookie.setMaxAge(604800); // 设置 cookie 的有效期为 7 天 7 * 24 * 60 * 60
                response.addCookie(tokenCookie);

                return ApiResponse.success(user);
            }else{
                return ApiResponse.fail("账号已经被注册或者服务器内部出现问题");
            }
        }else{
            return ApiResponse.fail("Phone number is not valid");
        }
    }
    @GetMapping("/get_code")
    public Map<String,Object> get_code(@RequestParam String phone_number) {
        String code = CodeCheck.generateCode();
        // todo 发送到手机上
        if (Redis.setValue(phone_number,code,60)){
            return ApiResponse.success(code);
        }
        return ApiResponse.fail();
    }
    @PostMapping("/login")
    public Map<String,Object> login(@RequestParam String phone, @RequestParam String code, HttpServletResponse response) {
        return ApiResponse.success();
    }
}
