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
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.List;
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
                Cookie userIdCookie = new Cookie("userId",user.getUserId());   // 此处不能直接在tokenCookie那添加,是无效的
                userIdCookie.setPath("/");
                userIdCookie.setMaxAge(604800);
                response.addCookie(userIdCookie);

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
    @PostMapping("/create_user")
    public Map<String,Object> create_user(@RequestParam String phoneNumber, @RequestParam String userName, @RequestParam int level,@RequestParam String avatarUrl) {
        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setUserName(userName);
        user.setLevel(level);
        user.setAvatarUrl(avatarUrl);
        return ApiResponse.success(userService.registerUser(user));
    }
    @PostMapping(value = "/upload_avatar")
    public Map<String,Object> upload_avatar(@RequestParam("image") MultipartFile file) {    // base64传输图片行不通，此处浏览器会自动将 / 改为%2F，还得自己手动编写解析代码 先解析转义字符再解析base64编码。
        try {
            if (file.isEmpty()) {
                return ApiResponse.fail("请选择要上传的文件");
            }

            // 保存文件
            Path uploadPath = Paths.get("C:\\Users\\26247\\Desktop\\zy\\NexusScale-Manage\\src\\main\\resources\\pic");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = "avatar" + System.currentTimeMillis() + ".png";
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            // 上传到OSS上
            String avatarUrl = AliYun.uploadFile(filePath.toString(),"avatar/"+fileName);
            System.out.println(avatarUrl);
            return ApiResponse.success(avatarUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail();
        }
    }

    @PostMapping("/delete_user")
    public Map<String,Object> delete_user(@RequestParam String userId) {
        return ApiResponse.success(userService.deleteUser(userId));
    }
    @PostMapping("/batch_delete_users")
    public Map<String,Object> batch_delete_users(@RequestBody List<String> ids) {
        System.out.println(ids);
        return ApiResponse.success(userService.batchDeleteUser(ids));
    }
    @PostMapping("/change_user_name") //给管理员修改其它用户的用户名用，也可以用户修改自己的个人信息时候调用
    public Map<String,Object> update_user(@RequestParam String userId, @RequestParam String userName) {
        User user = new User();
        user.setUserId(userId);
        user.setUserName(userName);
        return ApiResponse.success(userService.updateUser(user));
    }
    @PostMapping("/change_user_level")
    public Map<String,Object> update_user_level(@RequestParam String userId, @RequestParam int level) {
        User user = new User();
        user.setUserId(userId);
        user.setLevel(level);
        return ApiResponse.success(userService.updateUser(user));
    }
    @GetMapping("/search_users") //可以根据用户名、电话号码、角色来查找
     public Map<String,Object> search_users(@RequestParam int currentPage,@RequestParam int pageSize,@RequestParam String keyword) {

        Map<String, Object> map = ApiResponse.success(userService.searchUser(currentPage,pageSize,keyword));
        map.put("currentPage", currentPage);
        map.put("total", userService.getUserCount(keyword));
        map.put("pageSize", pageSize);
        return map;
    }
}
