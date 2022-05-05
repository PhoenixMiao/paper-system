package com.phoenix.paper.controller;

import com.phoenix.paper.service.CommentService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("评论相关操作")
@RestController
@RequestMapping("/comment")
@Validated
public class CommentController {

    @Autowired
    private CommentService commentService;


}
