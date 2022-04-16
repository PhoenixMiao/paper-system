package com.phoenix.read.service.impl;

import com.phoenix.read.common.CommonErrorCode;
import com.phoenix.read.common.CommonException;
import com.phoenix.read.controller.request.ReportRequest;
import com.phoenix.read.entity.Report;
import com.phoenix.read.mapper.PassageMapper;
import com.phoenix.read.mapper.ReportMapper;
import com.phoenix.read.mapper.UserMapper;
import com.phoenix.read.service.ReportService;
import com.phoenix.read.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PassageMapper passageMapper;

    @Override
    public Long report(ReportRequest request,Long userId){
        Report report=new Report(null,userId,request.getPassageId(),request.getReason(),0,TimeUtil.getCurrentTimestamp());
        return new Long(reportMapper.insert(report));
    }

    @Override
    public List<Report> getReportList(Long userId) {
        if(userMapper.selectByPrimaryKey(userId).getType()!=2)  throw new CommonException(CommonErrorCode.USER_NOT_SUPERADMIN);
        return reportMapper.selectAll();
    }

    @Override
    public void examReport(Long userId, Long reportId,Integer status) {
        if(userMapper.selectByPrimaryKey(userId).getType()!=2)  throw new CommonException(CommonErrorCode.USER_NOT_SUPERADMIN);
        reportMapper.examReport(status,reportId);
        if(status==1){
            passageMapper.deleteByPrimaryKey(reportMapper.selectByPrimaryKey(reportId).getPassageId());
            userMapper.muteUser(1,userId);
            new MemberThead(null,2,userId);
        }
    }
}
