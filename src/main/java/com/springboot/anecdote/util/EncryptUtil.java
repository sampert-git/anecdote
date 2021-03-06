package com.springboot.anecdote.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密工具类
 *
 * @author Sampert
 * @version 1.0
 * @date 2021/5/14 1:18
 */
public class EncryptUtil {

    /**
     * 构造函数，私有化，不允许在外部创建此工具类实例
     */
    private EncryptUtil(){}

    /**
     * 利用java原生的摘要实现SHA256加密
     *
     * @param str 待加密的明文
     * @return String 加密后的密文
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
     * @return String 16进制拼接结果（长度64位）
     */
    private static String byteToHex(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        String temp;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                // 1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
