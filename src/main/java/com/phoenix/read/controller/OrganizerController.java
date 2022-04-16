package com.phoenix.read.controller;

import com.phoenix.read.common.Result;
import com.phoenix.read.entity.Organizer;
import com.phoenix.read.service.OrganizerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api("主办方相关操作")
@RestController
@RequestMapping("/organizer")
@Validated
public class OrganizerController {
    @Autowired
    private OrganizerService organizerService;

    @GetMapping("/list")
    @ApiOperation(value = "获取主办方列表", response = Organizer.class)
    public Result  getOrganizerList(){
        return Result.success(organizerService.getOrganizerList());
    }

    @GetMapping("/name")
    @ApiOperation(value = "根据名称模糊搜索主办方",response = Organizer.class)
    @ApiImplicitParam(name = "name",value = "搜索内容",paramType = "query",dataType = "Organizer")
    public Result getOrganizerListByName(@RequestParam("name")String name){
        if(name==null) return Result.success(organizerService.getOrganizerList());
        else return Result.success(organizerService.getOrganizerListByName(name));
    }

}
