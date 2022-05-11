package com.phoenix.paper.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.phoenix.paper.annotation.Auth;
import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.common.Result;
import com.phoenix.paper.controller.request.AddPaperRequest;
import com.phoenix.paper.service.PaperService;
import com.phoenix.paper.util.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

@Api("论文相关操作")
@RestController
@RequestMapping("/paper")
@Validated
public class PaperController {

    @Autowired
    private PaperService paperService;

    @Autowired
    private SessionUtils sessionUtils;

    @GetMapping(value = "",produces = "application/json")
    @ApiOperation(value = "论文详情")
    @ApiImplicitParam(name = "paperId",value = "论文id",required = true,paramType = "query",dataType = "Long")
    public Result getPaperById( @RequestParam("paperId")Long paperId){
        try{
            return Result.success(paperService.getPaperById(paperId));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }

    }

    @GetMapping(value = "/list", produces = "application/json")
    @ApiOperation(value = "论文列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "orderBy", value = "排序规则(0为热度,1为论文在现实世界中发布时间)", required = true, paramType = "query", dataType = "Integer"),
    })
    public Result getPaperList(@NotNull @RequestParam("pageSize") Integer pageSize, @NotNull @Param("pageNum") Integer pageNum, @NotNull @Param("orderBy") Integer orderBy) {
        return Result.success(paperService.getPaperList(pageNum, pageSize, orderBy));
    }

    @Auth
    @PostMapping(value = "/add", produces = "application/json")
    @ApiOperation(value = "新增论文")
    public Result addPPaper(@NotNull @RequestBody AddPaperRequest addPaperRequest) {
        try {
            return Result.success(paperService.addPaper(sessionUtils.getUserId(), addPaperRequest));
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Auth
    @PostMapping(value = "/upload",produces = "application/json")
    @ApiImplicitParam(name = "paperId",value = "论文id",required = true,paramType = "query",dataType = "Long")
    public Result uploadNote(MultipartFile file, @NotNull @RequestParam("paperId")Long paperId){
        try{
            return Result.success(paperService.uploadPaper(file,paperId));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @GetMapping(value = "/download/{flag}", produces = "application/json")
    public Result downloadPaper(@PathVariable String flag, HttpServletResponse response) {
        OutputStream os;
        String basePath = System.getProperty("user.dir") + "/src/main/resources/files";
        List<String> fileNames = FileUtil.listFileNames(basePath);
        String fileName = fileNames.stream().filter(name -> name.contains(flag)).findAny().orElse("");
        if (fileName.equals("")) return Result.result(CommonErrorCode.FILE_NOT_EXIST);
        try {
            if (StrUtil.isNotEmpty(fileName)) {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                response.setContentType("application/octet-stream");
                byte[] bytes = FileUtil.readBytes(basePath + fileName);
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            return Result.result(CommonErrorCode.DOWNLOAD_FILE_FAILED);
        }
        return Result.success("下载成功");
    }

    @Auth
    @GetMapping(value = "/titles", produces = "application/json")
    @ApiOperation(value = "根据名称返回论文列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "title", value = "搜索内容", required = true, paramType = "query", dataType = "Integer"),
    })
    public Result findPaperByTitle(@RequestParam("pageSize") Integer pageSize, @Param("pageNum") Integer pageNum, @Param("title") String title) {
        return Result.success(paperService.findPaperByTitle(pageNum, pageSize, title));
    }

    @Auth
    @PostMapping(value = "/delete", produces = "application/json")
    @ApiOperation(value = "删除论文")
    @ApiImplicitParam(name = "paperId", value = "论文id", required = true, paramType = "query", dataType = "Long")
    public Result deletePaper(@NotNull @RequestParam("paperId") Long paperId) {
        try {
            paperService.deletePaper(paperId, sessionUtils.getUserId());
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
        return Result.success("删除成功");
    }

    @Auth
    @PostMapping(value = "/quote", produces = "application/json")
    @ApiOperation(value = "添加论文引用关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "quoterId", value = "引用者id", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "quotedId", value = "被引用论文id", required = true, paramType = "query", dataType = "Long"),
    })
    public Result deletePaper(@NotNull @RequestParam("quoterId") Long quoterId, @NotNull @RequestParam("quotedId") Long quotedId) {
        try {
            return Result.success(paperService.addPaperQuotation(quoterId, quotedId));
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }
}
