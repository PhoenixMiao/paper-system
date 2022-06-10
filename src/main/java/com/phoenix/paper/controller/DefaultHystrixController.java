//package com.phoenix.paper.controller;
//
//import com.phoenix.paper.common.CommonErrorCode;
//import com.phoenix.paper.common.Result;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @version V1.0
// * @author: hqk
// * @date: 2020/5/11 16:38
// * @Description: 网关熔断降级返回
// */
//@RestController
//public class DefaultHystrixController {
//
//
//    @RequestMapping("/defaultfallback")
//    public Object defaultfallback(){
//        System.out.println("降级操作...");
//
//        return Result.result(CommonErrorCode.FLOW_LIMITED);
//    }
//}