package com.phoenix.paper.common;

/**
 * 通用常量
 * @author yannis
 * @version 2020/8/1 16:50
 */
public class CommonConstants {

    public final static String SESSION = "session";
    public final static String APP_NAME = "sex-edu";
    public final static String SHADOW_TEST = "shadow-test";
    public final static String SEPARATOR = ",";
    public final static String CHAT_RECORD_COLLECTION_NAME = "chat_record";
    public final static String WX_SESSION_REQUEST_URL = "https://api.weixin.qq.com/sns/jscode2session";
    public final static String DOWNLOAD_PATH = "http://127.0.0.1:8000/note/download/";
    //https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_5_1.shtml
    public final static String WX_PAY_REQUEST_URL = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";
    public final static String CNY_CURRENCY = "CNY";
    public final static String SIGN_TYPE_RSA = "RSA";
    public final static String SIGN_TYPE_HMAC_SHA256 = "HMAC-SHA256";
    public final static String LANG_TYPE_ZH_CN = "zh_CN";
    public final static String[] PAPER_TYPE = {"理论证明型", "综述型", "实验型", "⼯具型", "数据集型"};
    public final static String[] SEARCH_PAPER_FIELDS = {"title", "context", "summary", "publishConference", "author", "uploader", "paperType", "publishDate", "link",};
}