package com.phoenix.paper.controller;

import com.phoenix.paper.annotation.Auth;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.common.Result;
import com.phoenix.paper.service.CommentService;
import com.phoenix.paper.util.SessionUtils;
import com.phoenix.paper.util.TimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@Api("评论相关操作")
@RestController
@RequestMapping("/comment")
@Validated
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private SessionUtils sessionUtils;

    @Auth
    @PostMapping("/delete")
    @ApiOperation(value = "删除评论")
    @ApiImplicitParam(name = "commentId",value = "评论id",required = true,paramType = "query",dataType = "Long")
    public Result deleteUser(@NotNull @RequestParam("commentId")Long commentId){
        try{
            commentService.deleteComment(commentId,sessionUtils.getUserId());
        }catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
        return Result.success("删除成功");
    }
}
