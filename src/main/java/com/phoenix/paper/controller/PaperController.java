package com.phoenix.paper.controller;


import com.phoenix.paper.annotation.Admin;
import com.phoenix.paper.annotation.Auth;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.common.Result;
import com.phoenix.paper.service.PaperService;
import com.phoenix.paper.util.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@Api("论文相关操作")
@RestController
@RequestMapping("/paper")
@Validated
public class PaperController {

    @Autowired
    private PaperService paperService;

    @Autowired
    private SessionUtils sessionUtils;

    @GetMapping("")
    @ApiOperation(value = "论文详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paperId",value = "论文id",required = true,paramType = "query",dataType = "Long"),})
    public Result getPaperById(@NotNull @RequestParam("paperId")Long paperId){
            return Result.success(paperService.getPaperById(paperId));
    }

    @GetMapping("/list")
    @ApiOperation(value = "论文列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize",value = "每页显示数量 (不小于0)",required = true,paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "orderBy", value = "排序规则", required = true, paramType = "query", dataType = "String"),
    })
    public Result getPaperList(@NotNull @RequestParam("pageSize") Integer pageSize, @NotNull @RequestParam("pageNum")Integer pageNum,@RequestParam("orderBy")String orderBy){
        return Result.success(paperService.getPaperList(pageNum,pageSize,orderBy));
    }

    @Auth
    @GetMapping("/myPaper")
    @ApiOperation(value = "我的论文列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize",value = "每页显示数量 (不小于0)",required = true,paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
    })
    public Result getMyPaperList(@NotNull @RequestParam("pageSize") Integer pageSize, @NotNull @RequestParam("pageNum")Integer pageNum){
        try{
            return Result.success(paperService.getUserPaperList(pageNum,pageSize, sessionUtils.getUserId()));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Admin
    @GetMapping("/userPaper")
    @ApiOperation(value = "用户论文列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize",value = "每页显示数量 (不小于0)",required = true,paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "query", dataType = "Long"),
    })
    public Result getUserPaperList(@NotNull @RequestParam("pageSize") Integer pageSize, @NotNull @RequestParam("pageNum")Integer pageNum,@NotNull @RequestParam("userId")Long userId){
        try{
            return Result.success(paperService.getUserPaperList(pageNum,pageSize, userId));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

}
