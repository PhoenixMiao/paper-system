package com.phoenix.read.service;

import com.phoenix.read.controller.request.ReportRequest;
import com.phoenix.read.entity.Report;

import java.util.List;

public interface ReportService {
    Long report(ReportRequest request,Long userId);

    List<Report> getReportList(Long userId);

    void examReport(Long userId,Long reportId,Integer status);
}
