package com.phoenix.read.controller;

import com.phoenix.read.annotation.Auth;
import com.phoenix.read.common.CommonException;
import com.phoenix.read.common.Result;
import com.phoenix.read.controller.request.AddResearchDirectionReuqest;
import com.phoenix.read.service.ResearchDirectionService;
import com.phoenix.read.util.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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

    //@Auth
    @PostMapping("/add")
    @ApiOperation(value = "添加研究方向")
    public Result updateUser(@NotNull @RequestBody AddResearchDirectionReuqest addResearchDirectionReuqest){
        Long userId=sessionUtils.getUserId();
        try{
            researchDirectionService.addResearchDirection(addResearchDirectionReuqest,userId);
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
        return Result.success("ok");
    }
}
