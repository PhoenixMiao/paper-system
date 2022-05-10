//package com.phoenix.paper.aspect;
//
//import com.phoenix.paper.bean.ServerFuse;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ResponseStatusException;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//
///**
// * RestController 切面
// *
// * @author miracle
// * @since 2021/11/22 17:46
// */
//@Slf4j
//@Aspect
//@Component
//public class RestControllerAspect {
//
//    @Resource
//    private ServerFuse serverFuse;
//
//    @Resource
//    private HttpServletRequest httpServletRequest;
//
//    /**
//     * 指定包路径下public方法的切入点
//     */
//    @Pointcut("execution(public * com.phoenix.paper.controller.*(..))")
//    public void inPublicMethod() {
//    }
//
//    /**
//     * RestController切入点
//     */
//    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
//    public void inRestController() {
//    }
//
//    @Around("inPublicMethod() && inRestController()")
//    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        //服务熔断（限流）：是否拦截请求，启用拦截会自增并发数
//        boolean fuse = serverFuse.interceptRequest(httpServletRequest.getRequestURI());
//        try {
//            if (fuse) {
//                //熔断：503服务不可用
//                String errorMessage = String.format("接口[%s]流量已达最大值[%s]，服务已熔断，请稍后再试！"
//                        , httpServletRequest.getRequestURI()
//                        , serverFuse.getRequestUriConcurrencyConfig(httpServletRequest.getRequestURI()));
//                //打印错误日志
//                log.error(errorMessage);
//                //不允许执行程序，直接熔断
//                if (!serverFuse.process()) {
//                    //服务超载：503
//                    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, errorMessage);
//                }
//            }
//
//            //执行业务程序
//            long startTime = System.currentTimeMillis();
//            Object result = proceedingJoinPoint.proceed();
//            log.info("PROCEED TIME : {}ms ", System.currentTimeMillis() - startTime);
//
//            return result;
//        } catch (Throwable throwable) {
//            log.error("PROCEED FAILED !", throwable);
//            throw throwable;
//        } finally {
//            //服务熔断（限流）：减少当前并发数
//            serverFuse.decrementAndGetRequestUrlConcurrency(httpServletRequest.getRequestURI());
//        }
//    }
//
//}
