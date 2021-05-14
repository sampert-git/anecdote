package com.springboot.anecdote.service;

import com.springboot.anecdote.entity.User;

import java.util.List;

public interface UserService {
    // 用户登录
    User userLogin(String account, String pwd);

    // 用户注册
    int userRegister(User user);

    // 用户权限修改
    int userPowerModify(User user);

    // 获取用户名集合
    List<String> findUserNames();

    // 根据用户id查找用户名
    String findUserNameById(Integer id);

    // 获取验证码
    boolean getVerifCode(String mailAddress);

    // 验证码比对
    boolean checkVerifCode(String mailAddress, String code);

    // 获取邮箱地址集合
    List<String> findEmails();

    // 根据账号（可能是用户名也可能是邮箱）获取用户信息
    User getUserByAccount(String account);
}
