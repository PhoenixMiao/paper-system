package com.phoenix.paper.util;

import com.phoenix.paper.common.CommonConstants;
import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.dto.SessionData;
import com.phoenix.paper.mapper.UserMapper;
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
    private ShuaiDatabaseUtils shuaiDatabaseUtil;

    @Autowired
    private UserMapper userMapper;

    public Long getUserId(){
        return Optional
                .ofNullable(getSessionData())
                .orElse(new SessionData())
                .getId();
    }


    public SessionData getSessionData() throws CommonException {
        String key = request.getHeader(CommonConstants.SESSION);
        if (key == null) throw new CommonException(CommonErrorCode.NEED_SESSION_ID);
        //if(!shuaiDatabaseUtil.hasKey(key)) throw new CommonException(CommonErrorCode.SESSION_IS_INVALID);
        if (shuaiDatabaseUtil.isExpire(key)) {
            shuaiDatabaseUtil.del(key);
            throw new CommonException(CommonErrorCode.LOGIN_HAS_OVERDUE);
        }
        return (SessionData) shuaiDatabaseUtil.get(key);
    }

    public void setSessionId(String sessionId){
        response.setHeader(CommonConstants.SESSION,sessionId);
    }

    public String generateSessionId() {
        String sessionId = UUID.randomUUID().toString();
        response.setHeader(CommonConstants.SESSION, sessionId);
        return sessionId;
    }

    public void ChangeContentType(){
        response.setHeader("Content-Type","application/json");
    }
}
