package com.phoenix.read.controller;

import com.phoenix.read.common.CommonException;
import com.phoenix.read.common.Result;
import com.phoenix.read.controller.request.GetPassageListRequest;
import com.phoenix.read.controller.request.ReportRequest;
import com.phoenix.read.entity.Passage;
import com.phoenix.read.entity.Push;
import com.phoenix.read.entity.Report;
import com.phoenix.read.service.ReportService;
import com.phoenix.read.util.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Api("举报相关操作")
@RestController
@RequestMapping("/report")
@Validated
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private SessionUtils sessionUtils;

    @PostMapping("")
    @ApiOperation(value = "举报", response = Long.class)
    public Result report(@NotNull @Valid @RequestBody ReportRequest request){
        return Result.success(reportService.report(request,sessionUtils.getUserId()));
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取举报列表",response = Report.class)
    public Result getReportList(){
        try {
            return Result.success(reportService.getReportList(sessionUtils.getUserId()));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode(),sessionUtils.getUserId());
        }
    }
}
