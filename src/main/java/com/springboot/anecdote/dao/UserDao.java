package com.springboot.anecdote.dao;

import com.springboot.anecdote.entity.User;

import java.util.List;

public interface UserDao {
    User userLogin(String account,String pwd);      // 用户登录
    int userRegister(User user);    // 用户注册
    int userPowerModify(User user); // 用户权限修改
    String findUserNameById(Integer id);    // 根据用户id查找用户名
    List<String> findUserNames();   // 获取用户名集合
    List<String> findEmails();      // 获取邮箱地址集合
    User getUserByAccount(String account);  // 根据账号（可能是用户名也可能是邮箱）获取用户信息
}
