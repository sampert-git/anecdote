package com.springboot.anecdote.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * 通用工具类（JDK8 环境）
 *
 * @author Sampert
 * @version 1.0
 * @date 2021/6/8
 */
public class CommonUtils {
    /**
     * 构造函数私有化，不允许在外部创建此工具类实例
     */
    private CommonUtils(){}

    /**
     * 利用java原生的摘要实现SHA256加密
     *
     * @param str 待加密的明文
     * @return java.lang.String 加密后的密文
     */
    public static String getSHA256Str(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            encodeStr = byteToHex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes 待转化byte数组
     * @return java.lang.String 16进制拼接结果（长度64位）
     */
    private static String byteToHex(byte[] bytes) {
        // 多线程环境用StringBuffer替代：StringBuffer strContent = new StringBuffer();
        StringBuilder strContent = new StringBuilder();
        String temp;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                // 1得到一位的进行补0操作
                strContent.append("0");
            }
            strContent.append(temp);
        }
        return strContent.toString();
    }

    /**
     * 获取随机验证码
     * @param randStart 每位随机数最小值（包含）
     * @param randEnd 每位随机数最大值（排除）
     * @param limitSize 随机数长度
     * @return java.lang.String 随机数字符串形式
     */
    public String getRandomCode(int randStart, int randEnd, int limitSize) {
        return new Random()
                .ints(randStart, randEnd)
                .limit(limitSize)
                .mapToObj(String::valueOf)
                .reduce("", String::concat);
    }
}
