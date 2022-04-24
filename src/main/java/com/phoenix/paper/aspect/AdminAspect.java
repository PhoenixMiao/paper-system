package com.phoenix.paper.aspect;

import com.phoenix.paper.annotation.Auth;
import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.dto.SessionData;
import com.phoenix.paper.util.AssertUtil;
import com.phoenix.paper.util.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class AdminAspect {

    @Autowired
    SessionUtils sessionUtil;

    @Around("@annotation(com.phoenix.paper.annotation.Admin)")
    public Object doAroundAdmin(ProceedingJoinPoint joinPoint) throws Throwable {

        SessionData sessionData = sessionUtil.getSessionData();

        AssertUtil.notNull(sessionData, CommonErrorCode.INVALID_SESSION);

        AssertUtil.isTrue(sessionData.getType()==1,CommonErrorCode.USER_NOT_ADMIN);

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        Auth annotation = method.getAnnotation(Auth.class);

        //log
        log.error("------------");
        log.error("operator: " + sessionData.getId());
        log.error("operation: " + method.getName());

        return joinPoint.proceed();
    }
}
