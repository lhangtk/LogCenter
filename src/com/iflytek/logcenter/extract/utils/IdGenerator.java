package com.iflytek.logcenter.extract.utils;

import java.util.Random;

/**
 * Created by hangli2 on 2014/9/23.
 *
 * 生成session_id，随机字符串
 */
public class IdGenerator {
    private String id;

    public IdGenerator(){//默认生成32位
        this.id = generate(32);
    }

    public static String generate(int length){
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String generate(){//默认生成32位
        return generate(32);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
