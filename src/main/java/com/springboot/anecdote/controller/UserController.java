package com.springboot.anecdote.controller;

import com.springboot.anecdote.entity.User;
import com.springboot.anecdote.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 用户（User）控制器
 *
 * @author Sampert
 * @version 1.0
 * @date 2020/9/12 9:24
 **/
@Controller
public class UserController {

    /** UserService 用户服务实例 */
    private UserService userService;
    /** 用户密码匹配器（6-20位大、小写英文或数字） */
    private static final Pattern PATTERN_PWD = Pattern.compile("^[A-Za-z0-9]{6,20}$");
    /** self4j 日志记录器 */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    /**
     * 构造函数
     * @param userService UserService 用户服务实例
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取验证码
     * @param mailAddress 接收验证码的邮箱
     * @return java.lang.String 获取成功返回 "ok"，否则返回 "fail"
     */
    @GetMapping("/user/getCode/{mailAddress}")
    @ResponseBody
    public String getVerifyCode(@PathVariable("mailAddress") String mailAddress) {
        return userService.getVerifyCode(mailAddress) ? "ok" : "fail";
    }

    /**
     * 验证码比对
     * @param mailAddress 接收验证码的邮箱
     * @param code 收到的验证码
     * @return java.lang.String 比对成功返回 "ok"，否则返回 "fail"
     */
    @GetMapping("/user/checkCode/{mailAddress}/{code}")
    @ResponseBody
    public String checkVerifyCode(@PathVariable("mailAddress") String mailAddress,
                                 @PathVariable("code") String code) {
        return userService.checkVerifyCode(mailAddress, code) ? "ok" : "fail";
    }

    /**
     * 用户注册
     * @param user 封装了用户信息的 User 实例
     * @return java.lang.String 注册成功返回 "ok"，否则返回 "fail"
     */
    @PostMapping("/user/register")
    @ResponseBody
    public String userRegister(User user) {
        if (PATTERN_PWD.matcher(user.getUserPwd()).matches()) {
            int result = userService.userRegister(user);
            if (result > 0) {
                LOGGER.info("用户:{}(email = {})注册成功！", user.getUserName(), user.getUserEmail());
                return "ok";
            } else {
                LOGGER.info("用户:{}(email = {})注册失败！", user.getUserName(), user.getUserEmail());
                return "fail";
            }
        }
        return "密码请使用6-20位大小写英文或数字！";
    }

    /**
     * 请求登录&注册页面
     * @param account 用户帐户（用户名或邮箱）
     * @param session HTTP会话
     * @return java.lang.String 存在账号cookie或HTTP会话返回跳转地址（站点首页），否则返回登录&注册页面
     */
    @GetMapping("/user/login")
    public String logRegDocument(@CookieValue(value = "anec_login_account", required = false) String account,
                                 HttpSession session) {
        // 如果客户端浏览器存在帐户cookie将直接跳转到列表请求；
        if (account != null || session.getAttribute("ANEC_USER_SESSION") != null) {
            return "redirect:/anec/list/1/client";
        }
        return "log-reg";
    }

    /**
     * 用户登录
     * @param account 用户帐户（用户名或邮箱）
     * @param pwd 密码
     * @param session HTTP会话
     * @return java.lang.String 登录成功返回 "ok"，否则返回 "fail"
     */
    @PostMapping("/user/login")
    @ResponseBody
    public String userLogin(@RequestParam("account") String account, @RequestParam("pwd") String pwd, HttpSession session) {
        User user = userService.userLogin(account, pwd);
        if (user == null) {
            LOGGER.warn("用户:{} 登录失败！", account);
            return "fail";
        }
        session.setAttribute("ANEC_USER_SESSION", user);
        LOGGER.info("用户:{} (id = {}) 登录成功！", user.getUserName(), user.getUserId());
        return "ok";
    }

    /**
     * 退出登录
     * @param response HTTP响应
     * @param session HTTP会话
     * @return java.lang.String 跳转地址（站点首页）
     */
    @GetMapping("/user/logout")
    public String userLogout(HttpServletResponse response, HttpSession session) {
        User user = (User) session.getAttribute("ANEC_USER_SESSION");
        LOGGER.info("用户:{}(id = {})退出登录！", user.getUserName(), user.getUserId());
        session.removeAttribute("ANEC_USER_SESSION");
        Cookie cookie = new Cookie("anec_login_account", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/anec/list/1/client";
    }

    /**
     * 用户权限修改
     * @param user 封装用户信息的 User 实例
     * @return java.lang.String 修改成功返回 "ok"，否则返回 "fail"
     */
    @PostMapping("/admin/user/updatePower")
    @ResponseBody
    public String updateUserPower(@RequestParam User user) {
        int result = userService.updateUserPower(user);
        if (result > 0) {
            return "ok";
        } else {
            return "fail";
        }
    }

    /**
     * 验证用户名是否可用（不存在则可用）
     * @param userName 用户名
     * @return java.lang.String 可用返回 "ok"，否则返回 "fail"
     */
    @GetMapping("/user/nameIfEna/{userName}")
    @ResponseBody
    public String ifEnabledUserName(@PathVariable("userName") String userName) {
        List<String> userNames = userService.listUserNames();
        return userNames.contains(userName) ? "fail" : "ok";
    }

    /**
     * 邮箱是否可用（不存在则可用）
     * @param email 邮箱地址
     * @return java.lang.String 可用返回 "ok"，否则返回 "fail"
     */
    @GetMapping("/user/emailIfEna/{email}")
    @ResponseBody
    public String ifEnableEmail(@PathVariable("email") String email) {
        List<String> emails = userService.listEmails();
        return emails.contains(email) ? "fail" : "ok";
    }
}
