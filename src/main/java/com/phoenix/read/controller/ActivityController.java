package com.phoenix.read.controller;

import com.phoenix.read.annotation.Auth;
import com.phoenix.read.common.CommonException;
import com.phoenix.read.common.Result;
import com.phoenix.read.controller.request.NewPushRequest;
import com.phoenix.read.controller.request.UpdateActivityRequest;
import com.phoenix.read.controller.response.NewPushResponse;
import com.phoenix.read.dto.BriefActivity;
import com.phoenix.read.entity.Push;
import com.phoenix.read.service.ActivityService;
import com.phoenix.read.util.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Api("活动相关操作")
@RestController
@RequestMapping("/activity")
@Validated
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private SessionUtils sessionUtils;

    @GetMapping("")
    @ApiOperation(value = "获取活动详情", response = Push.class)
    @ApiImplicitParam(name = "pushId", value = "活动id", required = true, paramType = "query", dataType = "Long")
    public Result getPushById(@NotNull @RequestParam("activityId") Long activityId) {
        return Result.success(activityService.getActivityById(activityId));
    }

    @Auth
    @PostMapping("/new")
    @ApiOperation(value = "补充新活动信息", response = Long.class)
    public Result newPush(@NotNull @Valid @RequestBody UpdateActivityRequest updateActivityRequest) {
        try {
            activityService.updateActivity(updateActivityRequest, sessionUtils.getUserId());
            return Result.success("操作成功");
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode(), sessionUtils.getUserId());
        }
    }

    @GetMapping("/list")
    @ApiOperation(value = "根据主办方获得活动列表",response = BriefActivity.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize",value = "每页显示数量 (不小于0)",required = true,paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "organizerId",value = "主办方id",required = true,paramType = "query",dataType = "Long"),
    })
    public Result getBriefActivityByOrganizer(@NotNull @RequestParam("pageSize")Integer pageSize,
                                              @NotNull @RequestParam("pageNum")Integer pageNum,
                                              @NotNull @RequestParam("organizerId")Long organizerId){
        return Result.success(activityService.getBriefActivityByOrganizer(pageSize,pageNum,organizerId));
    }

    @GetMapping("/my")
    @ApiOperation(value = "获取我发布的活动",response = BriefActivity.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize",value = "每页显示数量 (不小于0)",required = true,paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
    })
    public Result getBriefActivityByPublisher(@NotNull @RequestParam("pageSize")Integer pageSize,
                                              @NotNull @RequestParam("pageNum")Integer pageNum){
        try {
            return Result.success(activityService.getBriefActivityByOrganizer(pageSize,pageNum,sessionUtils.getUserId()));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode(),sessionUtils.getUserId());
        }
    }

}

