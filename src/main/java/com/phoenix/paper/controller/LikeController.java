package com.phoenix.paper.controller;

import com.phoenix.paper.annotation.Auth;
import com.phoenix.paper.common.Result;
import com.phoenix.paper.service.LikeService;
import com.phoenix.paper.service.impl.ScheduledTasks;
import com.phoenix.paper.util.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Api("点赞相关操作")
@RestController
@RequestMapping("/like")
@Validated
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private SessionUtils sessionUtils;

    @Auth
    @GetMapping(value = "",produces = "application/json")
    @ApiOperation(value = "点赞", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "对象id",required = true,paramType = "query",dataType = "Long"),
            @ApiImplicitParam(name = "type",value = "对象类型(0:论文 1:笔记)",required = true,paramType = "query",dataType = "Integer"),})
    public Result giveLike(@NotNull @RequestParam("id") Long objectId, @NotNull @Min(value=0,message="无效类型") @Max(value = 1,message ="无效类型" ) @RequestParam("type")Integer type) {
        return Result.success(likeService.like(objectId, type, sessionUtils.getUserId()));
    }


    @Auth
    @GetMapping(value = "/cancel",produces = "application/json")
    @ApiOperation(value = "取消点赞", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "对象id",required = true,paramType = "query",dataType = "Long"),
            @ApiImplicitParam(name = "type",value = "对象类型(0:论文 1:笔记)",required = true,paramType = "query",dataType = "Integer"),})
    public Result cancelLike(@NotNull @RequestParam("id") Long objectId, @NotNull @Min(value=0,message="无效类型") @Max(value = 1,message ="无效类型" ) @RequestParam("type") Integer type) {
        return Result.success(likeService.cancelLike(objectId, type, sessionUtils.getUserId()));
    }

}
