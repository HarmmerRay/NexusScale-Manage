package com.nexuscale.nexusscalemanage.controller;

import com.nexuscale.nexusscalemanage.dao.UserMapper;
import com.nexuscale.nexusscalemanage.service.UserService;
import com.nexuscale.nexusscalemanage.util.*;
import com.nexuscale.nexusscalemanage.entity.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    //CRUD  登录和注册都有了。
    @PostMapping("/register")
    public Map<String,Object> register(@RequestParam String phone_number, @RequestParam String code, HttpServletResponse response) {
        if (PhoneRegexCheck.isValidPhoneNumber(phone_number)){
            if (!CodeCheck.checkCode(phone_number,code)){
                return ApiResponse.fail("验证码不正确");
            }
            User user = new User();
            user.setUserId(UserId.getUserId(phone_number));
            user.setPhoneNumber(phone_number);
            user.setUserName(phone_number.substring(7,11));

            user = userService.registerUser(user);  // 用户不存在则注册 存在则登录
            if (user != null){
                // 生成 token 7天过期时间  7 * 24 * 60 * 60 * 1000  单位是毫秒
                String token = EncryptionDecryption.encrypt(user.getPhoneNumber(),new Date().getTime() + 604800000);
                // 创建一个 cookie 并将 token 放入其中,给前端存储Token使用（作为鉴权）
                Cookie tokenCookie = new Cookie("token", token);
                tokenCookie.setPath("/");
                tokenCookie.setMaxAge(604800); // 设置 cookie 的有效期为 7 天 7 * 24 * 60 * 60  单位是 秒
                response.addCookie(tokenCookie);

                // 在SpringBoot框架中 将user存储在请求的Session 会话中
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    HttpSession session = request.getSession();
                    session.setAttribute("userId", user.getUserId());
                }

                return ApiResponse.success(user);
            }else{
                return ApiResponse.fail("服务器内部出现问题");
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
    @PostMapping("/verify_token")
    public Map<String,Object> token_verify(HttpServletRequest request, HttpServletResponse response) {

        Cookie [] cookies = request.getCookies();
        for (Cookie cookie : cookies){
            if (cookie.getName().equals("token")){
                String token = cookie.getValue();
                String [] res = EncryptionDecryption.decrypt(token);
//                System.out.println(res[0]);
//                System.out.println(res[1]);
                return ApiResponse.success(res);
            }
        }
        return ApiResponse.fail();
    }
}
