package com.phoenix.read.controller;

import com.phoenix.read.annotation.Auth;
import com.phoenix.read.common.CommonException;
import com.phoenix.read.common.Result;
import com.phoenix.read.service.MemberService;
import com.phoenix.read.util.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Api("成员相关操作")
@RestController
@RequestMapping("/member")
@Validated
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private SessionUtils sessionUtils;

    @Auth
    @PostMapping("/make")
    @ApiOperation(value = "预约活动", response = Long.class)
    @ApiImplicitParam(name = "activityId", value = "活动id", required = true, paramType = "query", dataType = "Long")
    public Result makeOrder(@NotNull @RequestParam("activityId") Long activityId) {
        try{
            return Result.success(memberService.makeOrder(activityId, sessionUtils.getUserId()));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Auth
    @PostMapping("/check")
    @ApiOperation(value = "活动签到")
    public Result check(){
        try{
            memberService.check(sessionUtils.getUserId());
            return Result.success("签到成功");
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Auth
    @GetMapping("")
    @ApiOperation(value = "根据id获取参与情况详情(包括userId和activityId)")
    @ApiImplicitParam(name = "id",value = "memberId",required = true,paramType = "query",dataType = "Member")
    public Result getMemberById(@NotNull @RequestParam("id")Long id){
        return Result.success(memberService.getMemberById(id));
    }

    @Auth
    @GetMapping("/list")
    @ApiOperation(value = "获取个人预约列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize",value = "每页显示数量 (不小于0)",required = true,paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),})
    public Result getMemberList(@NotNull @RequestParam("pageSize")Integer pageSize,
                                @NotNull @RequestParam("pageNum")Integer pageNum){
        return Result.success(memberService.getMemberListByUserId(pageSize,pageNum,sessionUtils.getUserId()));
    }
}
