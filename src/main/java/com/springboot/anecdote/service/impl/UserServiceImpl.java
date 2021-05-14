package com.springboot.anecdote.service.impl;

import com.springboot.anecdote.dao.UserDao;
import com.springboot.anecdote.entity.User;
import com.springboot.anecdote.service.UserService;
import com.springboot.anecdote.util.EncryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Class UserServiceImpl
 * Description UserService实现类；
 * Date 2020/9/12 9:05
 *
 * @author Sampert
 * @version 1.0
 **/
@CacheConfig(cacheNames = "cacheUser")
@Service
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private JavaMailSender sender;
    private static final String CACHE_USER_NAME_LIST = "'userNameList'";
    private static final String CACHE_EMAIL_LIST = "'emailList'";
    private static final String CACHE_USER_NAME_PREFIX = "'userName_'";
    @Value("${spring.mail.username}")
    private String mailFrom;
    private ConcurrentHashMap<String, String> map;  // 存放随机验证码
    private ScheduledThreadPoolExecutor executor;   // 计划任务执行器

    @Autowired
    public UserServiceImpl(UserDao userDao, JavaMailSender sender) {
        this.userDao = userDao;
        this.sender = sender;
        map = new ConcurrentHashMap<>(16);    // TODO 不确定存放元素个数先写默认值16，确定后改为：个数/负载因子+1
        executor = new ScheduledThreadPoolExecutor(2);    // TODO　核心池线程数（根据实际情况调整）
        executor.setRemoveOnCancelPolicy(true); // 计划任务取消即删除
    }

    /**
     * 发送验证码
     * @param mailAddress 接收验证码的邮箱地址
     * @return boolean 验证码发送成功返回true
     */
    @Override
    public boolean getVerifCode(String mailAddress) {
        try {
            map.remove(mailAddress);    // 每次获取验证码前确保map中没有旧残留
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);  // 发出地址
            message.setTo(mailAddress);             // 接收地址
            message.setSubject("Anecdote验证码");   // 邮件主题
            // 生成随机6位数字验证码
            String randCode = new Random()
                    .ints(0, 10)
                    .limit(6)
                    .mapToObj(String::valueOf)
                    .reduce("", String::concat);
            message.setText("【Anecdote 名人轶事】您好，您的验证码为：" + randCode + "（5分钟内有效）！");    // 邮件内容
            sender.send(message);
            map.put(mailAddress, randCode);
            // 5分钟后移除指定邮件地址对应的验证码（如果此时map中不存在指定key不会做任何操作）
            executor.schedule(() -> map.remove(mailAddress), 5, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 验证码比对
    @Override
    public boolean checkVerifCode(String mailAddress, String code) {
        // 无论是否验证通过，调用一次此方法后即触发邮件地址对应验证码清除
        String mapCode = map.get(mailAddress);
        if (mapCode != null && mapCode.equals(code)) {
            map.remove(mailAddress);
            return true;
        }
        map.remove(mailAddress);
        return false;   // 验证码比对不一致或已过期
    }

    // 用户注册
    @Caching(evict = {@CacheEvict(key = CACHE_USER_NAME_LIST), @CacheEvict(key = CACHE_EMAIL_LIST)})
    @Override
    public int userRegister(User user) {
        // SHA-256 加密
        user.setUserPwd(EncryptUtil.getSHA256Str(user.getUserPwd()));
        return userDao.userRegister(user);
    }

    // 用户登录
    @Override
    public User userLogin(String account, String pwd) {
        // SHA-256 加密
        pwd = EncryptUtil.getSHA256Str(pwd);
        return userDao.userLogin(account, pwd);
    }

    // 用户权限修改
    @Override
    public int updateUserPower(User user) {
        return userDao.userPowerModify(user);
    }

    // 获取用户名集合
    @Cacheable(key = CACHE_USER_NAME_LIST)
    @Override
    public List<String> listUserNames() {
        return userDao.findUserNames();
    }

    // 获取邮箱地址集合
    @Cacheable(key = CACHE_EMAIL_LIST)
    @Override
    public List<String> listEmails() {
        return userDao.findEmails();
    }

    // 根据账号获取用户信息（账号可能是用户名也可能是邮箱地址）
    @Override
    public User getUserByAccount(String account) {
        return userDao.getUserByAccount(account);
    }

    // 根据用户id查找用户名
    @Cacheable(key = CACHE_USER_NAME_PREFIX + " + #id")
    @Override
    public String getUserNameById(Integer id) {
        return userDao.findUserNameById(id);
    }
}
