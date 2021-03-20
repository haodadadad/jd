package com.jit.jd.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";



    //第一次加密  网络传输

    public static String inputPassToFormPass(String inputPass){
        String str =""+ salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }



    public static String formPassToDBPass(String formPass,String salt){
        String str =""+salt.charAt(0)+salt.charAt(2)+formPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }


    //合并
    public static String inputPassToDbPass(String inputPass, String salt){
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, salt);
        return dbPass;
    }


    public static void main(String[] args) {
     //   ce21b747de5af71ab5c2e20ff0a60eea
        System.out.println(inputPassToFormPass("123456"));
        System.out.println(formPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9","1a2b3c4d"));
        System.out.println(inputPassToDbPass("123456","1a2b3c4d"));

    }

}
