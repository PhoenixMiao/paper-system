package com.phoenix.paper.util;

import com.phoenix.paper.single.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@SuppressWarnings("all")
public class ShuaiDatabaseUtils {

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        ShuaiString kkey = new ShuaiString(key);
        ShuaiDB db = ShuaiServer.dbActive;
        if (!db.getDict().containsKey(kkey)) return false;
        long expireTime = System.nanoTime() + (long) time * ShuaiConstants.ONT_NANO;
        ShuaiExpireKey expireKey = new ShuaiExpireKey(kkey);
        expireKey.setExpireTime(expireTime);
        db.getExpires().remove(expireKey);
        db.getExpires().put(expireKey);
        db.getDict().get(kkey).setExpireTime(expireTime);
        return true;
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        for (ShuaiString k : ShuaiServer.dbActive.getDict().keySet()) System.out.println(k);
        for (ShuaiObject v : ShuaiServer.dbActive.getDict().values()) System.out.println(v);
        ShuaiObject object = ShuaiServer.dbActive.getDict().get(new ShuaiString(key));
        System.out.println(key);
        for (ShuaiExpireKey key1 : ShuaiServer.dbActive.getExpires())
            System.out.println(key1 + " " + key1.getDelay(TimeUnit.MILLISECONDS));
        if (object == null) {
            System.out.println("object is null!");
            return 0;
        }
        System.out.println(object.getExpireTime());
        if (object == null || object.getExpireTime() <= 0 || System.nanoTime() > object.getExpireTime()) return 0;
        else return object.getExpireTime() / ShuaiConstants.ONT_NANO;
    }

    public boolean isExpire(String key) {
        return getExpire(key) <= 1;
    }

    /**
     * 删除缓存
     *
     * @param keys 可以传一个值 或多个
     */
    public void del(String... keys) {
        if (keys != null && keys.length > 0) {
            for (String key : keys) {
                ShuaiServer.dbActive.getDict().remove(new ShuaiString(key));
            }
        }
    }

    //============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        //获取activeDb的字典并直接取出
        if (key == null) return null;
        return ShuaiServer.dbActive.getDict().get(new ShuaiString(key));
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
//        String command = "SET " + key + " " + serialize(value);
//        String result = sendCommand(command);
//        return "OK".equalsIgnoreCase(result);

        //获取activeDb的字典并直接设置
        try {
            ShuaiServer.dbActive.getDict().put(new ShuaiString(key), (ShuaiObject) value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            set(key, value);
            expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Long hget(String key, Object item) {
//        String command = "HGET " + key + " " + item;
//        String result = sendCommand(command);
//        return Long.parseLong(result);
        ConcurrentHashMap<ShuaiString, ShuaiObject> dict = ShuaiServer.dbActive.getDict();
        try {
            ShuaiHash hash = (ShuaiHash) dict.get(new ShuaiString(key));
            ShuaiString res = hash.getHashMap().get(new ShuaiString(item.toString()));
            return Long.parseLong(res.toString());
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<String, String> hmget(String key) {
//        String command = "HGETALL " + key;
//        String resString = sendCommand(command);
//        return strToMap(resString);

        ConcurrentHashMap<ShuaiString, ShuaiObject> dict = ShuaiServer.dbActive.getDict();
        Map<String, String> res = new HashMap<>();
        try {
            ShuaiHash hash = (ShuaiHash) dict.get(new ShuaiString(key));
            for (ShuaiString field : hash.getHashMap().keySet()) {
                res.put(field.toString(), hash.getHashMap().get(field).toString());
            }
            return res;
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    private Map<String, String> strToMap(String resString) {//不知道能不能支持序列化
        String[] res = resString.split("\n");
        Map<String, String> map = new HashMap<>();
        String key = null, value = null;
        for (int i = 0; i < res.length; i++) {
            if (i % 2 == 0) {
                key = res[i].split(" ")[1];
            } else {
                value = res[i].split(" ")[1];
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, String> map) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("HMSET ").append(key).append(" ");
//        for(String field:map.keySet()){
//            String value = map.get(field).toString();
//            builder.append(field).append(" ").append(value).append("");
//        }
//        String result = sendCommand(builder.toString());
//        return "OK".equalsIgnoreCase(result);

        //获取activeDb的字典并直接设置
        ConcurrentHashMap<ShuaiString, ShuaiObject> dict = ShuaiServer.dbActive.getDict();
        ShuaiHash hash = new ShuaiHash();
        ConcurrentHashMap<ShuaiString, ShuaiString> trueMap = hash.getHashMap();
        try {
            for (String field : map.keySet()) {
                trueMap.put(new ShuaiString(field), new ShuaiString(map.get(field).toString()));
            }
            dict.put(new ShuaiString(key), hash);
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
        return true;
    }

}
