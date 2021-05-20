package com.springboot.anecdote;

import com.alibaba.druid.filter.config.ConfigTools;
import com.springboot.anecdote.util.EncryptUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class AnecdoteApplicationTests {

    @Test
    void contextLoads() {
    }
}

class MyTest{

    /** 日志记录器（抽象层使用slf4j，自动调用logback实现层） */
    private static final Logger logger = LoggerFactory.getLogger(AnecdoteApplicationTests.class);

    /**
     * 测试self4j+logback记录日志
     * 日志级别：trace < debug < info < warn < error
     * 默认级别：info（可通过配置文件"logging.level.root"属性修改）
     *
     * @date 2020/9/17 10:52
     * @return void
     */
    @Test
    void testLogging() {
        logger.trace("trace级别的日志");
        logger.debug("debug级别的日志");
        logger.info("info级别的日志");
        logger.warn("warn级别的日志");
        logger.error("error级别的日志");
    }

    @Test
    void testReference(){
        List<Pojo> pojos=new ArrayList<>();
        Pojo pojo=new Pojo();
        pojo.setId(1);
        pojos.add(pojo);
        for (Pojo pojo1:pojos){
            pojo1.setId(2);
        }
        System.out.println(pojos.get(0).getId());   // 2
    }

    @Test
    void testRandom(){
        String code=new Random()
                .ints(0, 10)
                .limit(6)
                .mapToObj(String::valueOf)
                .reduce("",String::concat);
        System.out.println(code);   // 随机6位数字验证码
    }

    @Test
    void testEncrypt() {
        String md5Pwd= DigestUtils.md5DigestAsHex("123456".getBytes());
        String sha256Pwd = EncryptUtil.getSHA256Str("123456");
        System.out.println("MD5:" + md5Pwd + "  length:" + md5Pwd.length()); // 32位密文
        System.out.println("SHA-256:" + sha256Pwd + "  length:" + sha256Pwd.length()); // 64位密文
    }

    @Test
    void druidEncrypt() throws Exception {
        String pwd="2020sqlmm";
        String[] keyPair = ConfigTools.genKeyPair(512);
        String privateKey=keyPair[0];
        String publicKey=keyPair[1];
        pwd=ConfigTools.encrypt(privateKey,pwd);
        System.out.println("pwd:"+pwd+"\npublicKey:"+publicKey);
    }

    @Test
    void testRootDir() {
        System.out.println(System.getProperty("user.dir")+File.separator);
        System.out.println(Paths.get(System.getProperty("user.dir")).getParent() +
                File.separator+"anecdote-upload"+File.separator);
    }

    @Test
    void testList() {
        // 移除（或增加）List元素正例
        List<String> list1 = new ArrayList<>(2);
        list1.add("1");
        list1.add("2");
//        list1.removeIf("1"::equals);  // 此行(hang)为JDK8提供的方法
        Iterator<String> iterator = list1.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if ("1".equals(item)) {
                iterator.remove();
            }
        }
        System.out.println("list1:" + list1.toString());

        // 反例
        List<String> list2 = new ArrayList<>(2);
        list2.add("1");
        list2.add("2");
        for (String s : list2) {
            if ("1".equals(s)) { // 1为条件结果同正例，2为条件抛异常 java.util.ConcurrentModificationException
                list2.remove(s);
            }
        }
        System.out.println("list2:" + list2.toString());
    }

    @Test
    void testSwitch() {
        switchMethod(null);
    }

    void switchMethod(String param) {
        // 当 switch 括号内的变量类型为 String 并且此变量为外部参数时，必须先进行 null 判断
        if (null == param) {
            System.out.println("switch 参数空！");
            return;
        }
        switch (param) { // 如果参数为 null ，会抛 NPE
            // 肯定不是进入这里
            case "sth":
                System.out.println("it's sth");
                break;
            // 也不是进入这里
            case "null":
                System.out.println("it's null");
                break;
            // 也不是进入这里
            default:
                System.out.println("default");
        }
    }
}

class Pojo {
    private int  id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

