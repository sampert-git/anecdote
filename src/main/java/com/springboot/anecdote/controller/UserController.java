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

/**
 * Class UserController
 * Description User控制器类；
 * Date 2020/9/12 9:24
 *
 * @author Sampert
 * @version 1.0
 **/
@Controller
public class UserController {

    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 获取验证码
    @GetMapping("/user/getCode/{mailAddress}")
    @ResponseBody
    public String getVerifCode(@PathVariable("mailAddress") String mailAddress) {
        return userService.getVerifCode(mailAddress) ? "ok" : "fail";
    }

    // 验证码比对
    @GetMapping("/user/checkCode/{mailAddress}/{code}")
    @ResponseBody
    public String checkVerifCode(@PathVariable("mailAddress") String mailAddress,
                                 @PathVariable("code") String code) {
        return userService.checkVerifCode(mailAddress, code) ? "ok" : "fail";
    }

    // 用户注册
    @PostMapping("/user/register")
    @ResponseBody
    public String userRegister(User user) {
        int result = userService.userRegister(user);
        if (result > 0) {
            logger.info("用户" + user.getUserName() + "(email=" + user.getUserEmail() + ")注册成功！");
            return "ok";
        } else {
            logger.info("用户" + user.getUserName() + "(email=" + user.getUserEmail() + ")注册失败！");
            return "fail";
        }
    }

    // 返回登录/注册页面
    @GetMapping("/user/login")
    public String logRegDocument(@CookieValue(value = "anec_login_account", required = false) String account, HttpSession session) {
        // 如果客户端浏览器存在帐户cookie将直接跳转到列表请求；
        if (account != null || session.getAttribute("ANEC_USER_SESSION") != null) {
            return "redirect:/anec/list/1/client";
        }
        return "log-reg";
    }

    // 用户登录
    @PostMapping("/user/login")
    @ResponseBody
    public String userLogin(@RequestParam("account") String account, @RequestParam("pwd") String pwd, HttpSession session) {
        User user = userService.userLogin(account, pwd);
        if (user != null) {
            session.setAttribute("ANEC_USER_SESSION", user);
            logger.info("用户" + user.getUserName() + "(id=" + user.getUserId() + ")登录成功！");
            return "ok";
        }
        logger.info("用户" + account + "登录失败！");
        return "fail";
    }

    // 退出登录
    @GetMapping("/user/logout")
    public String userLogout(HttpServletResponse response, HttpSession session) {
        User user = (User) session.getAttribute("ANEC_USER_SESSION");
        logger.info("用户" + user.getUserName() + "(id=" + user.getUserId() + ")退出登录！");
        session.removeAttribute("ANEC_USER_SESSION");
        Cookie cookie = new Cookie("anec_login_account", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/anec/list/1/client";
    }

    // 用户权限修改
    @PostMapping("/admin/user/modify")
    @ResponseBody
    public String userPowerModify(@RequestParam User user, Model model) {
        int result = userService.updateUserPower(user);
        if (result > 0) {
            model.addAttribute("modMsg", "修改成功!");
            return "修改成功";
        } else {
            model.addAttribute("regMsg", "修改失败！");
            return "修改失败";
        }
    }

    // 用户名是否可用（不存在则可用）
    @GetMapping("/user/nameIfEna/{userName}")
    @ResponseBody
    public String ifEnabledUserName(@PathVariable("userName") String userName) {
        List<String> userNames = userService.listUserNames();
        return !userNames.contains(userName) ? "ok" : "fail";
    }

    // 邮箱是否可用（不存在则可用）
    @GetMapping("/user/emailIfEna/{email}")
    @ResponseBody
    public String ifEnableEmail(@PathVariable("email") String email) {
        List<String> emails = userService.listEmails();
        return !emails.contains(email) ? "ok" : "fail";
    }
}
