package com.phoenix.paper.common;


import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yannis
 * @version 2020/7/23 0:51
 */
@Getter
public enum CommonErrorCode {

    INVALID_SESSION(2006,"会话丢失","登录已失效，请重新登录"),
    USER_NOT_EXIST(2001,"用户不存在","用户不存在"),
    USER_NOT_ADMIN(2002,"用户非管理员","用户非管理员"),
    NEED_SESSION_ID(2003,"未传入sessionId","请传入会话id"),
    LOGIN_HAS_OVERDUE(2004,"登录已过期","登录已过期"),
    SESSION_IS_INVALID(2005,"该session数据库里没有","请在header里key为session处对应有效的sessionId"),
    SELF_INFORMATION_UNWRITTEN(2007,"用户信息不完整，请先完善姓名、学号、院系、专业、年级等信息再进行操作","用户信息不完整，请先完善姓名、学号、院系、专业、年级等信息再进行操作"),
    USER_NOT_SUPERADMIN(2008,"用户不是管理员","用户不是管理员"),
    USER_IS_ADMIN(2009,"用户已经是管理员了","用户已经是管理员了"),
    COMMENT_IS_NOT_ALLOWED(2011,"不能对二级评论进行评论","不能对二级评论进行评论"),
    HAS_LIKED(2012,"该用户已经对该文章点过赞","该用户已经对该文章点过赞"),
    PASSWORD_NOT_RIGHT(2013,"密码不正确","密码不正确，请输入正确的密码"),
    REPETITIVE_DIRECTION(2014,"与已有方向重复","与已有研究方向重复，不可添加"),
    HAVE_NO_SON(2015,"该节点为叶子节点","请输入有子节点的节点id"),
    DOWNLOAD_FILE_FAILED(2016,"下载文件失败","请在浏览器地址栏中输入链接来测试，或者检查网络或系统状况"),
    READ_FILE_ERROR(2017,"读取文件失败","请检查文件格式之后重新上传文件"),
    PAPER_NOT_EXIST(2018,"该论文不存在","只能在目前存在的论文上添加笔记"),
    RESEARCH_DIRECTION_NOT_EXIST(2019,"该研究方向不存在","请输入存在的研究方向id"),
    NOTE_NOT_EXIST(2020,"该笔记不存在","请输入目前系统中存在的笔记"),
    FILE_NOT_EXIST(2021,"该文件不存在","请输入有效的文件名"),
    VERIFICATION_CODE_HAS_EXPIRED(2022,"验证码已过期","请重新申请发送验证码"),
    HAS_NOT_SENT_EMAIL(2023,"未发送验证码","请先用此邮箱申请验证码"),
    SEND_EMAIL_FAILED(2024,"发送邮件失败","请检查邮箱账号"),
    EMAIL_HAS_BEEN_SIGNED_UP(2025,"邮箱已被注册，请使用账号密码登录","如果忘记密码，请通过邮箱验证找回"),
    VERIFICATION_CODE_WRONG(2026,"邮箱验证码错误","请输入正确的邮箱验证码"),
    EMAIL_NOT_SIGNED_UP(2027,"该邮箱尚未注册账号","请输入正确的邮箱或先用此邮箱注册账号"),
    PASSWORD_NOT_QUANTIFIED(2028,"密码强度不够","请输入符合要求的密码"),
    UPDATE_FAILED(2029,"更新出现竞态条件","请稍后重试"),
    CAN_NOT_DELETE(2030,"您没有删除该实体的权限","请选择您能操作的实体进行删除"),
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