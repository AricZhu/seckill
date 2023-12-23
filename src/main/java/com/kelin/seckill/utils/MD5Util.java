package com.kelin.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5Util {
    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    public static final String salt = "1a2b3c4d";

    public static String inputPassToFromPass(String inputPass) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);

        return md5(str);
    }

    public static String fromPassToDBPass(String fromPass, String salt) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + fromPass + salt.charAt(5) + salt.charAt(4);

        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass, String salt) {
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = fromPassToDBPass(fromPass, salt);

        return dbPass;
    }

    public static void main(String[] args) {
        String s = MD5Util.inputPassToFromPass("123456");
        System.out.println(s);
        String s1 = MD5Util.inputPassToDBPass("123456", salt);
        System.out.println(s1);
        String s2 = MD5Util.fromPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9", salt);
        System.out.println(s2);
    }
}
