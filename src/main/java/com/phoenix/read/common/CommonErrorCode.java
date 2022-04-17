package com.phoenix.read.common;


import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yannis
 * @version 2020/7/23 0:51
 */
@Getter
public enum CommonErrorCode {

    //1打头是微信错误，其他是程序错误
    WX_LOGIN_BUSY(1002,"系统繁忙，此时请开发者稍候再试","微信小程序繁忙，请稍候再试"),
    WX_LOGIN_INVALID_CODE(1003,"无效的code","授权失败，请检查微信账号是否正常"),
    WX_LOGIN_FREQUENCY_REFUSED(1004,"请求太频繁，一分钟不能超过100次","请勿多次重复授权"),
    WX_LOGIN_UNKNOWN_ERROR(1005,"微信授权未知错误","微信异常，请稍后再试"),
    INVALID_SESSION(2006,"会话丢失","登录已失效，请重新登录"),
    USER_NOT_EXIST(2001,"用户不存在","用户不存在"),
    USER_NOT_ADMIN(2002,"用户非管理员","用户非管理员"),
    NEED_SESSION_ID(2003,"未传入sessionId","请传入会话id"),
    LOGIN_HAS_OVERDUE(2004,"预约结束时间在当前时间之前，请重新填写预约结束时间","预约结束时间在当前时间之前，请重新填写预约结束时间"),
    SESSION_IS_INVALID(2005,"该session数据库里没有","请在header里key为session处对应有效的sessionId"),
    ORDER_HAS_MADE(2006,"用户已预约该活动，不可重复预约","用户已预约该活动，不可重复预约"),
    SELF_INFORMATION_UNWRITTEN(2007,"用户信息不完整，请先完善姓名、学号、院系、专业、年级等信息再进行操作","用户信息不完整，请先完善姓名、学号、院系、专业、年级等信息再进行操作"),
    USER_NOT_SUPERADMIN(2008,"用户不是超级管理员","用户不是超级管理员"),
    USER_IS_ADMIN(2009,"用户已经是管理员了","用户已经是管理员了"),
    USER_IS_MUTE(2010,"用户正在禁言中","用户正在禁言中"),
    COMMENT_IS_NOT_ALLOWED(2011,"不能对二级评论进行评论","不能对二级评论进行评论"),
    HAS_LIKED(2012,"该用户已经对该文章点过赞","该用户已经对该文章点过赞"),
    PASSWORD_NOT_RIGHT(2013,"密码不正确","密码不正确，请输入正确的密码"),
    ;

    /**
     * 错误码
     */
    private final Integer errorCode;

    /**
     * 错误原因（给开发看的）
     */
    private final String errorReason;

    /**
     * 错误行动指示（给用户看的）
     */
    private final String errorSuggestion;

    CommonErrorCode(Integer errorCode, String errorReason, String errorSuggestion) {
        this.errorCode = errorCode;
        this.errorReason = errorReason;
        this.errorSuggestion = errorSuggestion;
    }

    @Override
    public String toString() {
        return "CommonErrorCode{" +
                "errorCode=" + errorCode +
                ", errorReason='" + errorReason + '\'' +
                ", errorSuggestion='" + errorSuggestion + '\'' +
                '}';
    }

    //use for json serialization
    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("errorCode",errorCode);
        map.put("errorReason",errorReason);
        map.put("errorSuggestion",errorSuggestion);
        return map;
    }


}