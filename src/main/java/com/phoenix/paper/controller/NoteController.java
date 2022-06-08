package com.phoenix.paper.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.phoenix.paper.annotation.Auth;
import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.common.Result;
import com.phoenix.paper.controller.request.AddNoteRequest;
import com.phoenix.paper.controller.request.SearchNoteRequest;
import com.phoenix.paper.controller.request.UpdateNoteRequest;
import com.phoenix.paper.entity.Note;
import com.phoenix.paper.service.NoteService;
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
    @PostMapping(value = "/add", produces = "application/json")
    @ApiOperation(value = "增加笔记空壳及相关信息")
    public Result addNote(@NotNull @RequestBody AddNoteRequest addNoteRequest) {
        try {
            return Result.success(noteService.addNote(addNoteRequest));
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Auth
    @PostMapping(value = "/upload", produces = "application/json")
    @ApiOperation(value = "上传笔记封面（请先使用add接口增加笔记相关信息，并且add和upload之间不要有其他接口调用（对单个用户来说））但是这个接口可以不调用，也就是不加封面")
    @ApiImplicitParam(name = "noteId", value = "笔记id", required = true, paramType = "query", dataType = "Long")
    public Result uploadCover(MultipartFile file, @NotNull @RequestParam("noteId") Long noteId) {
        try {
            return Result.success(noteService.uploadCover(file, noteId));
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }

    @GetMapping(value = "/download/{flag}",produces = "application/json")
    @ApiOperation(value = "下载笔记附件（pdf或markdown）,整个链接可以通过note_link获得或者upload接口曾经给过你")
    public Result downloadNote(@PathVariable String flag, HttpServletResponse response){
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

    @GetMapping(value = "/list",produces = "application/json")
    @ApiOperation(value = "笔记列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize",value = "每页显示数量 (不小于0)",required = true,paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "orderBy", value = "排序规则(0为热度,1为发布时间)", required = true, paramType = "query", dataType = "Integer"),
    })
    public Result getNoteList(@NotNull @RequestParam("pageSize")int pageSize,@NotNull @RequestParam("pageNum")int pageNum,@NotNull @RequestParam("orderBy")int orderBy){
        return Result.success(noteService.getNoteList(pageSize,pageNum,orderBy));
    }

    @PostMapping(value = "/search",produces = "application/json")
    @ApiOperation(value = "搜索笔记")
    public Result searchNote(@NotNull @RequestBody SearchNoteRequest searchNoteRequest){
        return Result.success(noteService.searchNoteByBody(searchNoteRequest));
    }

    @GetMapping(value = "",produces = "application/json")
    @ApiOperation(value = "笔记详情")
    @ApiImplicitParam(name = "noteId",value = "笔记id",required = true,paramType = "query",dataType = "Long")
    public Result getNoteDetails(@NotNull @RequestParam("noteId")Long noteId){
        try {
            Note note = noteService.getNoteDetails(noteId);
            if (note.getAuthorId().equals(sessionUtils.getUserId()) || sessionUtils.getSessionData().getCanModify() == 1 || sessionUtils.getSessionData().getType() == 1)
                return Result.success("该用户有编辑和修改权限", note);
            else return Result.success("该用户无编辑和修改权限", note);
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Auth
    @PostMapping(value = "/delete",produces = "application/json")
    @ApiOperation(value = "删除笔记")
    @ApiImplicitParam(name = "noteId", value = "笔记id", required = true, paramType = "query", dataType = "Long")
    public Result deleteNote(@NotNull @RequestParam("noteId") Long noteId) {
        try {
            noteService.deleteNote(noteId);
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
        return Result.success("删除成功");
    }

    @GetMapping(value = "/searchByQuery", produces = "application/json")
    @ApiOperation(value = "对于笔记的快速搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "keyword", value = "搜索框中输入的内容", required = true, paramType = "query", dataType = "String")
    })
    public Result searchByQuery(@NotNull @RequestParam("pageSize") Integer pageSize, @NotNull @Param("pageNum") Integer pageNum, @Param("keyword") String keyword) {
        try {
            return Result.success(noteService.searchNoteByQuery(keyword, pageNum, pageSize));
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Auth
    @PostMapping(value = "/update", produces = "application/json")
    @ApiOperation(value = "更新笔记")
    @ApiImplicitParam(name = "noteId", value = "笔记id", required = true, paramType = "query", dataType = "Long")
    public Result updatePaper(@NotNull @RequestParam("noteId") Long noteId, @NotNull @RequestBody UpdateNoteRequest updateNoteRequest) {
        try {
            noteService.updateNote(noteId, updateNoteRequest);
            return Result.success("更新成功");
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }
}
