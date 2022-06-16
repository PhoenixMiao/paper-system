package com.phoenix.paper.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用常量
 *
 * @author yannis
 * @version 2020/8/1 16:50
 */
public class CommonConstants {

    public final static String SESSION = "session";
    public final static String APP_NAME = "paper";
    public final static String SHADOW_TEST = "shadow-test";
    public final static String WX_SESSION_REQUEST_URL = "https://api.weixin.qq.com/sns/jscode2session";
    public final static String DOWNLOAD_PAPER_PATH = "http://124.222.112.118:8079/paper/download/";
    public final static String DOWNLOAD_NOTE_PATH = "http://124.222.112.118:8079/note/download/";
    public final static String CNY_CURRENCY = "CNY";
    public final static String SIGN_TYPE_RSA = "RSA";
    public final static String SIGN_TYPE_HMAC_SHA256 = "HMAC-SHA256";
    public final static String LANG_TYPE_ZH_CN = "zh_CN";
    public final static String[] PAPER_TYPE = {"理论证明型", "综述型", "实验型", "⼯具型", "数据集型"};
    public final static String[] SEARCH_PAPER_FIELDS = {"title", "context", "summary", "publishConference", "author", "uploader", "paperType", "link",};
    public final static String[] SEARCH_NOTE_FIELDS = {"title", "context", "author"};
    public final static String PAPER_FILE_PATH = "/home/ubuntu/file/paper/";
    public final static String NOTE_FILE_PATH = "/home/ubuntu/file/note/";
    public final static String USER_FILE_PATH = "/home/ubuntu/file/user/";
    public static final Map<String, Float> SEARCH_PAPER_FIELDS_BOOST = Collections.unmodifiableMap(new HashMap<String, Float>() {
        private static final long serialVersionUID = 1L;

        {
            put("title", 5F);
            put("context", 3F);
            put("summary", 3F);
            put("publishConference", 4F);
            put("author", 2F);
            put("uploader", 2F);
            put("paperType", 4F);
            put("link", 1F);
        }
    });
    public static final Map<String, Float> SEARCH_NOTE_FIELDS_BOOST = Collections.unmodifiableMap(new HashMap<String, Float>() {
        private static final long serialVersionUID = 1L;

        {
            put("title", 5F);
            put("context", 3F);
            put("author", 2F);
        }
    });
}