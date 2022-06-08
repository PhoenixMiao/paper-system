package com.phoenix.paper.common;

/**
 * 通用常量
 * @author yannis
 * @version 2020/8/1 16:50
 */
public class CommonConstants {

    public final static String SESSION = "session";
    public final static String APP_NAME = "paper";
    public final static String SHADOW_TEST = "shadow-test";
    public final static String WX_SESSION_REQUEST_URL = "https://api.weixin.qq.com/sns/jscode2session";
    public final static String DOWNLOAD_PAPER_PATH = "http://124.222.112.118:8010/paper/download/";
    public final static String DOWNLOAD_NOTE_PATH = "http://124.222.112.118:8010/note/download/";
    public final static String CNY_CURRENCY = "CNY";
    public final static String SIGN_TYPE_RSA = "RSA";
    public final static String SIGN_TYPE_HMAC_SHA256 = "HMAC-SHA256";
    public final static String LANG_TYPE_ZH_CN = "zh_CN";
    public final static String[] PAPER_TYPE = {"理论证明型", "综述型", "实验型", "⼯具型", "数据集型"};
    public final static String[] SEARCH_PAPER_FIELDS = {"title", "context", "summary", "publishConference", "author", "uploader", "paperType", "link",};
    public final static String DIR_PATH = "/home/ubuntu/file/";
}