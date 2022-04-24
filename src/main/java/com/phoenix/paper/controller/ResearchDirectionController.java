package com.phoenix.paper.controller;

import com.phoenix.paper.annotation.Admin;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.common.Result;
import com.phoenix.paper.controller.request.AddResearchDirectionRequest;
import com.phoenix.paper.service.ResearchDirectionService;
import com.phoenix.paper.util.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@Api("研究方向相关操作")
@RestController
@RequestMapping("/direction")
@Validated
public class ResearchDirectionController {

    @Autowired
    private ResearchDirectionService researchDirectionService;

    @Autowired
    private SessionUtils sessionUtils;

    @Admin
    @PostMapping("/add")
    @ApiOperation(value = "添加研究方向")
    public Result updateUser(@NotNull @RequestBody AddResearchDirectionRequest addResearchDirectionRequest){
        Long userId=sessionUtils.getUserId();
        try{
            return Result.success(researchDirectionService.addResearchDirection(addResearchDirectionRequest,userId));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }
}
