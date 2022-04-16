package com.phoenix.read.controller;

import com.phoenix.read.annotation.Auth;
import com.phoenix.read.common.CommonException;
import com.phoenix.read.common.Result;
import com.phoenix.read.service.LikeService;
import com.phoenix.read.util.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("")
    @ApiOperation(value = "点赞", response = Long.class)
    @ApiImplicitParam(name = "passageId", value = "论坛id", required = true, paramType = "query", dataType = "Long")
    public Result Like(@NotNull @RequestParam("passageId") Long passageId) {
        Long userId = sessionUtils.getUserId();
        return Result.success(likeService.like(passageId, userId));
    }

    @Auth
    @GetMapping("/cancel")
    @ApiOperation(value = "取消点赞", response = String.class)
    @ApiImplicitParam(name = "likeId", value = "点赞id", required = true, paramType = "query", dataType = "Long")
    public Result cancelLike(@NotNull @RequestParam("likeId") Long likeId) {
        return Result.success(likeService.cancelLike(likeId));
    }


    @Auth
    @GetMapping("/check")
    @ApiOperation(value = "验证是否已经点赞", response = Long.class)
    @ApiImplicitParam(name = "passageId", value = "文章id", required = true, paramType = "query", dataType = "Long")
    public Result isLike(@NotNull @RequestParam("passageId") Long passageId) {
        Long userId = sessionUtils.getUserId();
        try {
            return Result.success(likeService.like(passageId, userId));
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }
}
