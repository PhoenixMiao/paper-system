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
import io.swagger.models.auth.In;
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

    @GetMapping("")
    @ApiOperation(value = "论文详情")
    @ApiImplicitParam(name = "paperId",value = "论文id",required = true,paramType = "query",dataType = "Long")
    public Result getPaperById( @RequestParam("paperId")Long paperId){
        try{
            return Result.success(paperService.getPaperById(paperId));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }

    }

    @GetMapping("/list")
    @ApiOperation(value = "论文列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize",value = "每页显示数量 (不小于0)",required = true,paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "orderBy", value = "排序规则(0为热度,1为论文在现实世界中发布时间)", required = true, paramType = "query", dataType = "Integer"),
    })
    public Result getPaperList(@RequestParam("pageSize") Integer pageSize, @Param("pageNum")Integer pageNum,@Param("orderBy")Integer orderBy){
        return Result.success(paperService.getPaperList(pageNum,pageSize,orderBy));
    }

    @Auth
    @PostMapping("/add")
    public Result addPPaper() {
        try {
            return Result.success(paperService.addPaper(sessionUtils.getUserId()));
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Auth
    @PostMapping("/upload")
    @ApiImplicitParam(name = "paperId",value = "论文id",required = true,paramType = "query",dataType = "Long")
    public Result uploadNote(MultipartFile file, @NotNull @RequestParam("paperId")Long paperId){
        try{
            return Result.success(paperService.uploadPaper(file,paperId));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @GetMapping("/download/{flag}")
    public Result downloadPpaer(@PathVariable String flag, HttpServletResponse response){
        OutputStream os;
        String basePath = System.getProperty("user.dir") + "/src/main/resources/files";
        List<String> fileNames = FileUtil.listFileNames(basePath);
        String fileName = fileNames.stream().filter(name -> name.contains(flag)).findAny().orElse("");
        if(fileName.equals("")) return Result.result(CommonErrorCode.FILE_NOT_EXIST);
        try{
            if(StrUtil.isNotEmpty(fileName)){
                response.addHeader("Content-Disposition","attachment;filename=" + URLEncoder.encode(fileName,"UTF-8"));
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
}
