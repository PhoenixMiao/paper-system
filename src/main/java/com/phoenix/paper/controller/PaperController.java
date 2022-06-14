package com.phoenix.paper.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.phoenix.paper.annotation.Auth;
import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.common.Result;
import com.phoenix.paper.controller.request.AddPaperRequest;
import com.phoenix.paper.controller.request.SearchPaperRequest;
import com.phoenix.paper.controller.request.UpdatePaperRequest;
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

import static com.phoenix.paper.common.CommonConstants.PAPER_FILE_PATH;

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
            return Result.success(paperService.addPaper(addPaperRequest));
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
        List<String> fileNames = FileUtil.listFileNames(PAPER_FILE_PATH);
        String fileName = fileNames.stream().filter(name -> name.contains(flag)).findAny().orElse("");
        if (fileName.equals("")) return Result.result(CommonErrorCode.FILE_NOT_EXIST);
        try {
            if (StrUtil.isNotEmpty(fileName)) {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                response.setContentType("application/octet-stream");
                byte[] bytes = FileUtil.readBytes(PAPER_FILE_PATH + fileName);
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
            paperService.deletePaper(paperId);
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
        return Result.success("删除成功");
    }


    @PostMapping(value = "/searchByBody", produces = "application/json")
    @ApiOperation(value = "论文搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "orderBy", value = "排序规则(0为热度,1为论文在现实世界中发布时间)", required = true, paramType = "query", dataType = "Integer"),
    })
    public Result searchByBody(@NotNull @RequestParam("pageSize") Integer pageSize, @NotNull @Param("pageNum") Integer pageNum, @NotNull @Param("orderBy") Integer orderBy, @NotNull @RequestBody SearchPaperRequest searchPaperRequest) {
        return Result.success(paperService.searchPaperByDirection(pageNum, pageSize, orderBy, searchPaperRequest));
    }

    @Auth
    @PostMapping(value = "/quote/add", produces = "application/json")
    @ApiOperation(value = "添加引用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "quoterId", value = "当前论文id", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "quotedId", value = "被引用的论文id", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "remarks", value = "备注", required = true, paramType = "query", dataType = "String"),
    })
    public Result addQuotation(@NotNull @RequestParam("quoterId") Long quoterId, @NotNull @Param("quotedId") Long quotedId, @Param("remarks") String remarks) {
        return Result.success(paperService.addQuotation(quoterId, quotedId, remarks));
    }

    @Auth
    @GetMapping(value = "/before", produces = "application/json")
    @ApiOperation(value = "获取在此篇论文之前发布的所有论文的列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "quoterId", value = "当前论文id", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "title", value = "想要查询的论文的标题（模糊查询）", required = true, paramType = "query", dataType = "String"),
    })
    public Result getBefore(@NotNull @RequestParam("quoterId") Long quoterId, @Param("title") String title) {
        try {
            return Result.success(paperService.searchPaperBefore(quoterId, title));
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Auth
    @PostMapping(value = "/quote/update", produces = "application/json")
    @ApiOperation(value = "更改文献引用的备注信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "quotationId", value = "论文引用id（创建论文的过程中给过）", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "remarks", value = "备注", required = true, paramType = "query", dataType = "String"),
    })
    public Result updateQuotation(@NotNull @RequestParam("quotationId") Long quotationId, @Param("remarks") String remarks) {
        try {
            paperService.updateQuotation(quotationId, remarks);
            return Result.success("更新备注成功");
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Auth
    @PostMapping(value = "/quote/delete", produces = "application/json")
    @ApiOperation(value = "删除文献引用")
    @ApiImplicitParam(name = "quotationId", value = "论文引用id（创建论文的过程中给过）", required = true, paramType = "query", dataType = "Long")
    public Result deleteQuotation(@NotNull @RequestParam("quotationId") Long quotationId) {
        try {
            paperService.deleteQuotation(quotationId);
            return Result.success("删除成功");
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }

    }

    @Auth
    @PostMapping(value = "/direction/add", produces = "application/json")
    @ApiOperation(value = "增加该论文的研究方向（仅用于后期更改论文研究方向时）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paperId", value = "论文id", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "directionId", value = "研究方向id", required = true, paramType = "query", dataType = "String"),
    })
    public Result addDirection(@NotNull @RequestParam("paperId") Long paperId, @Param("directionId") Long directionId) {
        return Result.success(paperService.addDirection(paperId, directionId));
    }

    @Auth
    @PostMapping(value = "/direction/delete", produces = "application/json")
    @ApiOperation(value = "删除论文研究方向（仅用于后期更改论文研究方向时）")
    @ApiImplicitParam(name = "paperDirectionId", value = "论文-研究方向id（创建论文的过程中给过）", required = true, paramType = "query", dataType = "Long")
    public Result deleteDirection(@NotNull @RequestParam("paperDirectionId") Long paperDirectionId) {
        try {
            paperService.deleteDirection(paperDirectionId);
            return Result.success("删除成功");
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Auth
    @PostMapping(value = "/update", produces = "application/json")
    @ApiOperation(value = "更新论文基本信息")
    @ApiImplicitParam(name = "paperId", value = "论文id", required = true, paramType = "query", dataType = "Long")
    public Result updatePaper(@NotNull @RequestParam("paperId") Long paperId, @NotNull @RequestBody UpdatePaperRequest updatePaperRequest) {
        try {
            paperService.updatePaper(paperId, updatePaperRequest);
            return Result.success("更新成功");
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }


    @GetMapping(value = "/searchByQuery", produces = "application/json")
    @ApiOperation(value = "对于论文的快速搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "keyword", value = "搜索框中输入的内容", required = true, paramType = "query", dataType = "String")
    })
    public Result searchByQuery(@NotNull @RequestParam("pageSize") Integer pageSize, @NotNull @Param("pageNum") Integer pageNum, @Param("keyword") String keyword) {
        try {
            return Result.success(paperService.searchPaper(keyword, pageNum, pageSize));
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }
}
