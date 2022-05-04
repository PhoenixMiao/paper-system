package com.phoenix.paper.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.phoenix.paper.annotation.Auth;
import com.phoenix.paper.common.*;
import com.phoenix.paper.controller.request.SearchNoteRequest;
import com.phoenix.paper.entity.Note;
import com.phoenix.paper.service.NoteService;
import com.phoenix.paper.util.SessionUtils;
import io.netty.handler.codec.CodecException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Api("笔记相关操作")
@RestController
@RequestMapping("/note")
@Validated
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private SessionUtils sessionUtils;

    @Auth
    @PostMapping("/add")
    @ApiImplicitParam(name = "paperId",value = "论文id",required = true,paramType = "query",dataType = "Long")
    public Result addNote(@NotNull @RequestParam("paperId")Long paperId){
        try{
            return Result.success(noteService.addNote(sessionUtils.getUserId(),paperId));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Auth
    @PostMapping("/upload")
    @ApiImplicitParam(name = "noteId",value = "笔记id",required = true,paramType = "query",dataType = "Long")
    public Result uploadNote(MultipartFile file,@NotNull @RequestParam("noteId")Long noteId){
        try{
            return Result.success(noteService.uploadNote(file,noteId));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @GetMapping("/download/{flag}")
    public Result downloadNote(@PathVariable String flag, HttpServletResponse response){
        OutputStream os;
        String basePath = System.getProperty("user.dir") + "/src/main/resources/files";
        List<String> fileNames = FileUtil.listFileNames(basePath);
        String fileName = fileNames.stream().filter(name -> name.contains(flag)).findAny().orElse("");
        if(fileName=="") return Result.result(CommonErrorCode.FILE_NOT_EXIST);
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

    @GetMapping("/list")
    @ApiOperation(value = "笔记列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize",value = "每页显示数量 (不小于0)",required = true,paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "orderBy", value = "排序规则(0为热度,1为发布时间)", required = true, paramType = "query", dataType = "Integer"),
    })
    public Result getNoteList(@NotNull @RequestParam("pageSize")int pageSize,@NotNull @RequestParam("pageNum")int pageNum,@NotNull @RequestParam("orderBy")int orderBy){
        return Result.success(noteService.getNoteList(pageSize,pageNum,orderBy));
    }

    @GetMapping("/search")
    @ApiOperation(value = "搜索笔记")
    public Result searchNote(@NotNull @RequestBody SearchNoteRequest searchNoteRequest){
        return Result.success(noteService.searchNote(searchNoteRequest));
    }
}
