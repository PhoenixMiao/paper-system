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
    @PostMapping(value = "/add",produces = "application/json")
    @ApiOperation(value = "添加研究方向")
    public Result updateUser(@NotNull @RequestBody AddResearchDirectionRequest addResearchDirectionRequest){
        Long userId=sessionUtils.getUserId();
        try{
            return Result.success(researchDirectionService.addResearchDirection(addResearchDirectionRequest,userId));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @GetMapping(value = "/son", produces = "application/json")
    @ApiOperation(value = "根据节点id获取该节点的子节点列表(若为0则获取根节点列表，若为-1则获取全部节点)")
    @ApiImplicitParam(name = "id", value = "节点在表中的行id", required = true, paramType = "query", dataType = "Long")
    public Result getSons(@NotNull @RequestParam("id") Long id){
        try{
            return Result.success(researchDirectionService.getSons(id));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @GetMapping(value = "/allson",produces = "application/json")
    @ApiOperation(value = "根据节点id获取该节点的所有小辈节点（包括自己、儿子、孙子，一直到叶子")
    @ApiImplicitParam(name = "id",value = "节点在表中的行id",paramType = "query",required = true,dataType = "Long")
    public Result getAllSons(@NotNull @RequestParam("id") Long id){
        try{
            return Result.success(researchDirectionService.getAllSons(id));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @GetMapping(value = "",produces = "application/json")
    @ApiOperation(value = "根据id获取研究方向名")
    @ApiImplicitParam(name = "id",value = "研究方向id",paramType = "query",required = true,dataType = "Long")
    public Result getResearchDirectionName(@NotNull @RequestParam("id") Long id){
        try{
            return Result.success(researchDirectionService.getResearchDirectionName(id));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Admin
    @PostMapping(value = "delete",produces = "application/json")
    @ApiOperation(value = "删除某一研究方向")
    @ApiImplicitParam(name = "id",value = "节点再表中的行id",paramType = "query",required = true,dataType = "Long")
    public Result deleteNode(@NotNull @RequestParam("id")Long id){
        try{
            researchDirectionService.deleteNode((id));
            return Result.success("操作成功");
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Admin
    @PostMapping(value = "/rename", produces = "application/json")
    @ApiOperation(value = "重命名某一研究方向")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "研究方向的节点id",required = true,paramType = "query",dataType = "Long"),
            @ApiImplicitParam(name = "name",value = "需要重命名的新名字",required = true,paramType = "query",dataType = "Integer"),})
    public Result authorizeUser(@NotNull @RequestParam("id")Long id,@NotNull @RequestParam("name")String name){
        try{
            researchDirectionService.updateNode(id,name);
        }catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
        return Result.success("重命名成功");
    }
}
