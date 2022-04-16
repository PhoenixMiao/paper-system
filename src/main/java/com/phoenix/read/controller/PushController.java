package com.phoenix.read.controller;


import com.phoenix.read.annotation.Auth;
import com.phoenix.read.common.CommonErrorCode;
import com.phoenix.read.common.CommonException;
import com.phoenix.read.common.Result;
import com.phoenix.read.controller.request.NewPushRequest;
import com.phoenix.read.controller.response.NewPushResponse;
import com.phoenix.read.dto.BriefPush;
import com.phoenix.read.entity.Push;
import com.phoenix.read.service.PushService;
import com.phoenix.read.util.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Api("推送相关操作")
@RestController
@RequestMapping("/push")
@Validated
public class PushController {
    @Autowired
    private PushService pushService;

    @Autowired
    private SessionUtils sessionUtils;

    @Auth
    @PostMapping("/new")
    @ApiOperation(value = "新建活动的推送", response = Long.class)
    public Result newPush(@NotNull @Valid @RequestBody NewPushRequest newPushRequest) {
        try{
            NewPushResponse newPushResponse = pushService.newPush(newPushRequest, sessionUtils.getUserId());
            return Result.success(newPushResponse);
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode(),sessionUtils.getUserId());
        }
    }

    @GetMapping("")
    @ApiOperation(value = "获取推送详情", response = Push.class)
    @ApiImplicitParam(name = "pushId",value = "推送id",required = true,paramType = "query",dataType = "Long")
    public Result getPushById(@NotNull @RequestParam("pushId")Long pushId) {
        return Result.success(pushService.getPushById(pushId));
    }

    @PostMapping("/summarize")
    @ApiOperation(value = "发布活动总结推送",response = NewPushResponse.class)
    public Result summarizePush(@NotNull @Valid @RequestBody NewPushRequest newPushRequest) {
        try{
            NewPushResponse newPushResponse = pushService.summarizePush(newPushRequest, sessionUtils.getUserId());
            return Result.success(newPushResponse);
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode(),sessionUtils.getUserId());
        }
    }


    @GetMapping("/list")
    @ApiOperation(value = "根据活动分类获取推送",response = BriefPush.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize",value = "每页显示数量 (不小于0)",required = true,paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "activityType",value = "主办方id",required = true,paramType = "query",dataType = "Long"),
    })
    public Result getBriefPushList(@NotNull @RequestParam("pageSize")Integer pageSize,
                                   @NotNull @RequestParam("pageNum")Integer pageNum,
                                   @NotNull @RequestParam("activityType")Integer activityType){
        return Result.success(pushService.getPushListByActivityType(pageSize,pageNum,activityType));
    }

    @GetMapping("/rolling")
    @ApiOperation(value = "获得四张滚动图",response = BriefPush.class)
    public Result getRollingAd(){
        return Result.success(pushService.rollingAd());
    }

}
