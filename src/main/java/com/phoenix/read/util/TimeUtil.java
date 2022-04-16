package com.phoenix.read.util;

import io.swagger.models.auth.In;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yannis
 * @version 2020/11/7 9:40
 */
@Component
public class TimeUtil {

    public static String getCurrentTimestamp(){
        String time = new Timestamp(System.currentTimeMillis()).toString();
        time = time.substring(0,19);
        return time;
    }

    public static String getCurrentTimestampForWxSend(String time){
        time = time.substring(0,16);
        StringBuilder sb = new StringBuilder(time);
        sb.replace(4,5,"年");
        sb.replace(7,8,"月");
        sb.replace(10,10,"日");
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(getCurrentTimestampForWxSend(getCurrentTimestamp()));
    }

}
