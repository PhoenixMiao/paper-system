package com.phoenix.paper.util;

import java.sql.Timestamp;

//通知消息模板
public class MessageUtil {

    //收藏公司+积分
    public static String addCollection(String company_name,Double addCollection_point,Double point){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return "您于 " + getNowTime() + " 收藏了公司 " + company_name + " ，积分+" + addCollection_point + "，当前积分为" + point + "分";
    }

    //打开公司网址+积分
    public static String openURL(String company_url,Double openURL_point,Double point){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return "您于 " + getNowTime() + " 打开了公司链接 " + company_url + " ，积分+" + openURL_point + "，当前积分为" + point + "分";
    }

    //更新报价+积分
    public static String setContent(Double setContent_point,Double point){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return "您于 " + getNowTime() + " 更新了公司报价，积分+" + setContent_point + "，当前积分为" + point + "分";
    }

    //充值会员+积分
    public static String rechargeMember(Double rechargeMember_point,Double point){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return "您于 " + getNowTime() + " 充值了会员，积分+" + rechargeMember_point + "，当前积分为" + point + "分";
    }

    public static String inviteToSetContent(String username,double invite_point, Double point) {
        return "您的好友 " + username +" 于 " + getNowTime() + " 第一次发布公司报价，您的积分+" + invite_point + "，当前积分为" + point + "分";
    }

    //获取现在的时间
    private static String getNowTime(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String timeStr=timestamp
                .toString()
                .substring(0, timestamp.toString().indexOf("."));
        return timeStr;
    }
}
