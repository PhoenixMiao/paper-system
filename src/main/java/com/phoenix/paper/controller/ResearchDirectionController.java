package com.phoenix.paper.controller;

import com.phoenix.paper.annotation.Admin;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.common.Result;
import com.phoenix.paper.controller.request.AddResearchDirectionRequest;
import com.phoenix.paper.service.ResearchDirectionService;
import com.phoenix.paper.util.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/son")
    @ApiOperation(value = "根据节点id获取该节点的子节点列表(若为0则获取根节点列表)")
    @ApiImplicitParam(name = "id",value = "节点在表中的行id",required = true,paramType = "query",dataType = "Long")
    public Result getSons(@NotNull @RequestParam("id") Long id){
        try{
            return Result.success(researchDirectionService.getSons(id));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @GetMapping("/allson")
    @ApiOperation(value = "根据节点id获取该节点的所有小辈节点（包括自己、儿子、孙子，一直到叶子")
    @ApiImplicitParam(name = "id",value = "节点在表中的行id",paramType = "query",required = true,dataType = "Long")
    public Result getAllSons(@NotNull @RequestParam("id") Long id){
        try{
            return Result.success(researchDirectionService.getAllSons(id));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }
}
