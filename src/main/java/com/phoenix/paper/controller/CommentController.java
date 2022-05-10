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
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    @PostMapping(value = "/delete",produces = "application/json")
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

    @Auth
    @PostMapping(value = "",produces = "application/json")
    @ApiOperation(value = "评论", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "对象id",required = true,paramType = "query",dataType = "Long"),
            @ApiImplicitParam(name = "type",value = "对象类型(0:论文 1:笔记 2:评论)",required = true,paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "content",value = "内容",required = true,paramType = "query",dataType = "String"),})
    public Result addComment(@NotNull @RequestParam("id") Long objectId, @NotNull @Min(value=0,message="无效类型") @Max(value = 2,message ="无效类型" ) @RequestParam("type")Integer objectType, @RequestParam("content")String content) {
        commentService.addComment(objectId, objectType, sessionUtils.getUserId(),content);
        return Result.success("添加成功");
    }


    @GetMapping(value = "/list",produces = "application/json")
    @ApiOperation(value = "获取评论列表", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "对象id",required = true,paramType = "query",dataType = "Long"),
            @ApiImplicitParam(name = "type",value = "对象类型(0:论文 1:笔记 2:评论)",required = true,paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize",value = "每页显示数量 (不小于0)",required = true,paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),})
    public Result getCommentList(@NotNull @RequestParam("id") Long objectId,@NotNull @RequestParam("type")Integer objectType,
                                 @NotNull @RequestParam("pageSize") Integer pageSize,@NotNull @RequestParam("pageNum")Integer pageNum) {
        return Result.success(commentService.getCommentList(objectId,objectType,pageSize,pageNum));
    }
}
