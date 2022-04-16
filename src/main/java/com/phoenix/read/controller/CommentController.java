package com.phoenix.read.controller;

import com.phoenix.read.annotation.Auth;
import com.phoenix.read.common.CommonException;
import com.phoenix.read.common.Result;
import com.phoenix.read.controller.request.CommentRequest;
import com.phoenix.read.controller.request.NewPushRequest;
import com.phoenix.read.entity.Comment;
import com.phoenix.read.entity.Push;
import com.phoenix.read.service.CommentService;
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

@Api("评论相关操作")
@RestController
@RequestMapping("/comment")
@Validated
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private SessionUtils sessionUtils;

    @GetMapping("")
    @ApiOperation(value = "获取评论详情", response = Comment.class)
    @ApiImplicitParam(name = "commentId", value = "评论id", required = true, paramType = "query", dataType = "Long")
    public Result getCommentById(@NotNull @RequestParam("commentId") Long commentId) {
        return Result.success(commentService.getCommentById(commentId));
    }

    @GetMapping("/comments")
    @ApiOperation(value = "获取评论id列表", response = Long.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "objectId", value = "对象id", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "objectType",value = "0为文章评论，1为对评论的评论", required = true, paramType = "query", dataType = "Integer")

    })
    public Result getCommentsById(@NotNull @RequestParam("objectId") Long objectId,
                                  @NotNull @RequestParam("objectType") Integer objectType) {
        return Result.success(commentService.getCommentsIdByIdAndType(objectId,objectType));
    }

    @Auth
    @PostMapping("/add")
    @ApiOperation(value = "评论", response = String.class)
    public Result getCommentsById(@NotNull @Valid @RequestBody CommentRequest commentRequest) {
        Long userId=sessionUtils.getUserId();
        try{
            return Result.success(commentService.addComment(commentRequest,userId));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }

    }
}
