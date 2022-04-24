package com.phoenix.paper.util;

import cn.hutool.crypto.SecureUtil;
import org.springframework.stereotype.Component;


@Component
public class PasswordUtil {

    public String convert(String origin){
        return SecureUtil
                .md5(origin)
                .substring(3,13);
    }


    public static void main(String[] args) {
        String password = "123456";
        PasswordUtil passwordUtil = new PasswordUtil();
        System.out.println(SecureUtil.md5(password));
        System.out.println(passwordUtil.convert(password));
    }

}
