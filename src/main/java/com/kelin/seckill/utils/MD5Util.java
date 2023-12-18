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
        String str = salt + inputPass;

        return md5(str);
    }

    public static String fromPassToDBPass(String fromPass, String salt) {
        String str = salt + fromPass;

        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass, String salt) {
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = fromPassToDBPass(fromPass, salt);

        return dbPass;
    }
}
