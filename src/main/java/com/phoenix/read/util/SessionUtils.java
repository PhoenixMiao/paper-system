package com.phoenix.read.util;

import com.phoenix.read.common.CommonConstants;
import com.phoenix.read.dto.SessionData;
import com.phoenix.read.entity.User;
import com.phoenix.read.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;


/**
 * @author yannis
 * @version 2020/8/1 18:38
 */
@Component
public class SessionUtils {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private RedisUtils redisUtil;

    @Autowired
    private UserMapper userMapper;

    public Long getUserId(){
        return Optional
                .ofNullable(getSessionData())
                .orElse(new SessionData())
                .getId();
    }

//    public Integer getUserType(){
//        return Optional
//                .ofNullable(getSessionData())
//                .orElse(new SessionData())
//                .getType();
//    }

    public SessionData getSessionData(){
        String key = request.getHeader(CommonConstants.SESSION);
        if(key == null)return null;

        try {
            if(redisUtil.isExpire(key)) return null;
            return (SessionData) redisUtil.get(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void setSessionId(String sessionId){
        response.setHeader(CommonConstants.SESSION,sessionId);
    }

    public String generateSessionId(){
        String sessionId = UUID.randomUUID().toString();
        response.setHeader(CommonConstants.SESSION,sessionId);
        return sessionId;
    }

    //todo
    public void invalidate(){
        request.removeAttribute(CommonConstants.SESSION);
    }

    private SessionData getSessionDataFromDB(String key) {
        SessionData sessionData;
        User user = userMapper.selectOne(User.builder().sessionId(key).build());
        if(user != null){
            sessionData = new SessionData(user);
            redisUtil.set(key,sessionData);
            return sessionData;
        }else{
            redisUtil.set(key,null,3600);
            return null;
        }
    }
}
