package com.phoenix.read.controller;

import com.phoenix.read.controller.request.SearchRequest;
import com.phoenix.read.service.PassageService;
import com.phoenix.read.service.PushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Api("搜索相关操作")
@RestController
@RequestMapping("/search")
@Validated
public class SearchController {

    @Autowired
    private PushService pushService;

    @Autowired
    private PassageService passageService;


    @PostMapping("/condition")
    @ApiOperation(value = "根据条件筛选信息")
    public Object search(@NotNull @Valid @RequestBody SearchRequest searchRequest) {
        if(searchRequest.getType()==1) return pushService.searchPush(searchRequest);
        if(searchRequest.getType()==2) return passageService.searchPassage(searchRequest);
        return "type值必须为1或2";
    }
}
