package com.springboot.anecdote.entity;

import java.io.Serializable;

/**
 * 用户（User）实体类
 *
 * @author Sampert
 * @version 1.0
 * @date 2020/9/11 18:03
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Integer userId;
    /** 用户名 */
    private String userName;
    /** 用户密码 */
    private String userPwd;
    /** 用户权限（1管理员，2普通用户） */
    private Integer userPower;
    /** 用户邮箱 */
    private String userEmail;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public int getUserPower() {
        return userPower;
    }

    public void setUserPower(int userPower) {
        this.userPower = userPower;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPwd='" + userPwd + '\'' +
                ", userPower=" + userPower +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
