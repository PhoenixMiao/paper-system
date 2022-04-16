package com.phoenix.read.controller;

import cn.hutool.db.Session;
import com.phoenix.read.annotation.Auth;
import com.phoenix.read.common.CommonException;
import com.phoenix.read.common.Result;
import com.phoenix.read.controller.request.GetPassageListRequest;
import com.phoenix.read.controller.request.NewPushRequest;
import com.phoenix.read.controller.request.PostRequest;
import com.phoenix.read.entity.Organizer;
import com.phoenix.read.entity.Passage;
import com.phoenix.read.entity.Push;
import com.phoenix.read.service.PassageService;
import com.phoenix.read.util.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Api("论坛相关操作")
@RestController
@RequestMapping("/passage")
@Validated
public class PassageController {

    @Autowired
    private PassageService passageService;

    @Autowired
    private SessionUtils sessionUtils;

    @PostMapping("/list")
    @ApiOperation(value = "获取论坛列表", response = Passage.class)
    public Result  getPassageList(@NotNull @Valid @RequestBody GetPassageListRequest getPassageListRequest){
        return Result.success(passageService.getPassageList(getPassageListRequest));
    }

    @GetMapping("")
    @ApiOperation(value = "获取论坛详情", response = Passage.class)
    @ApiImplicitParam(name = "passageId",value = "论坛id",required = true,paramType = "query",dataType = "Long")
    public Result getPassageById(@NotNull @RequestParam("passageId")Long passageId) {
        return Result.success(passageService.getPassageById(passageId));
    }

    @Auth
    @PostMapping("/post")
    @ApiOperation(value = "发布新贴", response = String.class)
    public Result  postNewPassage(@NotNull @Valid @RequestBody PostRequest postRequest){
        Long userId=sessionUtils.getUserId();
        try{
            return Result.success(passageService.postNewPassage(postRequest,userId));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode(),userId);
        }

    }
}
