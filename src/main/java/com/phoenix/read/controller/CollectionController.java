package com.phoenix.read.controller;

import com.phoenix.read.annotation.Auth;
import com.phoenix.read.common.Result;
import com.phoenix.read.service.CollectionService;
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

@Api("收藏相关操作")
@RestController
@RequestMapping("/collect")
@Validated
public class CollectionController {

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private CollectionService collectionService;

    @Auth
    @GetMapping("")
    @ApiOperation(value = "收藏", response = Long.class)
    @ApiImplicitParam(name = "passageId", value = "论坛id", required = true, paramType = "query", dataType = "Long")
    public Result Like(@NotNull @RequestParam("passageId") Long passageId) {
        Long userId=sessionUtils.getUserId();
        return Result.success(collectionService.collect(passageId,userId));
    }

    @Auth
    @GetMapping("/cancel")
    @ApiOperation(value = "取消收藏", response = String.class)
    @ApiImplicitParam(name = "collectionId", value = "收藏id", required = true, paramType = "query", dataType = "Long")
    public Result cancelLike(@NotNull @RequestParam("collectionId") Long collectionId) {
        return Result.success(collectionService.cancelCollection(collectionId));
    }
}
