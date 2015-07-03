package com.iflytek.logcenter.extract.utils;

/**
 * Created by hangli2 on 2014/11/12.
 */
public class StrToUnicode {
    public static String strToUnicode(String str){
        String unicode="";

        for (int i = 0; i < str.length(); i++) {

            // 取出每一个字符
            char c = str.charAt(i);

            // 转换为unicode
            unicode+= "\\u" +Integer.toHexString(c);
        }

        return unicode.toString();
    };
}
