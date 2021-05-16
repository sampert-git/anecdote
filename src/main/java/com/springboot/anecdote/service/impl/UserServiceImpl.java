package com.springboot.anecdote.service.impl;

import com.springboot.anecdote.dao.UserDao;
import com.springboot.anecdote.entity.User;
import com.springboot.anecdote.service.UserService;
import com.springboot.anecdote.util.EncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * UserService 实现类
 *
 * @author Sampert
 * @version 1.0
 * @date 2020/9/12 9:05
 **/
@CacheConfig(cacheNames = "cacheUser")
@Service
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private JavaMailSender sender;
    private RedisTemplate<String, String> redisTemplate;
    private static final String CACHE_USER_NAMES = "cacheUser";
    private static final String CACHE_USER_NAME_LIST = "'userNameList'";
    private static final String CACHE_EMAIL_LIST = "'emailList'";
    private static final String CACHE_USER_NAME_PREFIX = "'userName_'";
    @Value("${spring.mail.username}")
    private String mailFrom;
    private ConcurrentHashMap<String, String> map;  // 存放随机验证码
    private ScheduledThreadPoolExecutor executor;   // 计划任务执行器
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserDao userDao, JavaMailSender sender, RedisTemplate<String, String> redisTemplate) {
        this.userDao = userDao;
        this.sender = sender;
        this.redisTemplate = redisTemplate;
        map = new ConcurrentHashMap<>(16);    // 不确定存放元素个数先写默认值16，确定后改为：个数/负载因子+1
        executor = new ScheduledThreadPoolExecutor(2);    // 核心池线程数（根据实际情况调整）
        executor.setRemoveOnCancelPolicy(true); // 计划任务取消即删除
    }

    /**
     * 获取验证码
     * @param mailAddress 接收验证码的邮箱地址
     * @return boolean 验证码发送成功返回true
     */
    @Override
    public boolean getVerifyCode(String mailAddress) {
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

    /**
     * 验证码比对
     * @param mailAddress 获取验证码的邮箱
     * @param code 验证码
     * @return boolean 比对成功返回true ，比对不一致或验证码已过期返回 false
     */
    @Override
    public boolean checkVerifyCode(String mailAddress, String code) {
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
    @Override
    public int userRegister(User user) {
        // SHA-256 加密
        user.setUserPwd(EncryptUtil.getSHA256Str(user.getUserPwd()));
        int result = userDao.userRegister(user);
        // 如果注册成功
        if (result > 0) {
            ListOperations<String, String> listOperations = redisTemplate.opsForList();

            // 动态向用户名列表缓存添加注册成功的用户名
            String userNameKey = CACHE_USER_NAMES + "::" + CACHE_USER_NAME_LIST.substring(1, CACHE_USER_NAME_LIST.length() - 1);
            listOperations.rightPush(userNameKey, user.getUserName());

            // 动态向邮箱列表缓存添加注册成功的邮箱
            String emailKey = CACHE_USER_NAMES + "::" + CACHE_EMAIL_LIST.substring(1, CACHE_EMAIL_LIST.length() - 1);
            listOperations.rightPush(emailKey, user.getUserEmail());
        }
        return result;
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
    @Override
    public List<String> listUserNames() {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        String userNameKey = CACHE_USER_NAMES + "::" + CACHE_USER_NAME_LIST.substring(1, CACHE_USER_NAME_LIST.length() - 1);
        // 如果有缓存，从缓存获取数据
        if (Boolean.TRUE.equals(redisTemplate.hasKey(userNameKey))) {
            List<String> userNameList = listOperations.range(userNameKey, 0, -1);
            if (userNameList == null) {
                userNameList = new ArrayList<>(2);
            }
            LOGGER.info("从缓存获取用户名列表：{}", userNameList.toString());
            return userNameList;
        }
        // 没有缓存，则先从数据库获取数据，再放入缓存
        List<String> userNameListResult = userDao.findUserNames();
        listOperations.rightPushAll(userNameKey, userNameListResult);
        return userNameListResult;
    }

    // 获取邮箱地址集合
    @Override
    public List<String> listEmails() {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        String emailKey = CACHE_USER_NAMES + "::" + CACHE_EMAIL_LIST.substring(1, CACHE_EMAIL_LIST.length() - 1);
        // 如果有缓存，从缓存获取数据
        if (Boolean.TRUE.equals(redisTemplate.hasKey(emailKey))) {
            List<String> emailList = listOperations.range(emailKey, 0, -1);
            if (emailList == null) {
                emailList = new ArrayList<>(2);
            }
            LOGGER.info("从缓存获取邮箱列表：{}", emailList.toString());
            return emailList;
        }
        // 没有缓存，则先从数据库获取数据，再放入缓存
        List<String> emailListResult = userDao.findEmails();
        listOperations.rightPushAll(emailKey, emailListResult);
        return emailListResult;
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
