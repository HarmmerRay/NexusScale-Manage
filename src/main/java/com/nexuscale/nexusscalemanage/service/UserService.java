package com.nexuscale.nexusscalemanage.service;

import com.nexuscale.nexusscalemanage.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(User user);
    int deleteUser(String userId);
    int batchDeleteUser(List<String> userIds);
    int updateUser(User user);
    List<User> searchUser(int currentPage,int pageSize,String keyword);
    long getUserCount(String keyword);
}
