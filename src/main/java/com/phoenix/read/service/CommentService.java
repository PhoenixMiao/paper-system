package com.phoenix.read.service;

import com.phoenix.read.common.Result;
import com.phoenix.read.controller.request.CommentRequest;
import com.phoenix.read.entity.Activity;
import com.phoenix.read.entity.Comment;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface CommentService {

    Comment getCommentById(Long id);

    Long addComment(CommentRequest commentRequest,Long userId);

    List<Long> getCommentsIdByIdAndType(Long commentId,Integer commentType);
}
