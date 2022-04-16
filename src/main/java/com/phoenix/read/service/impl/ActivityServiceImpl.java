package com.phoenix.read.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.read.common.CommonErrorCode;
import com.phoenix.read.common.CommonException;
import com.phoenix.read.common.Page;
import com.phoenix.read.controller.request.UpdateActivityRequest;
import com.phoenix.read.dto.BriefActivity;
import com.phoenix.read.entity.Activity;
import com.phoenix.read.entity.User;
import com.phoenix.read.mapper.ActivityMapper;
import com.phoenix.read.mapper.UserMapper;
import com.phoenix.read.service.ActivityService;
import com.phoenix.read.util.DatesUtil;
import com.phoenix.read.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void updateActivity(UpdateActivityRequest updateActivityRequest, Long userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        Activity activity = activityMapper.getActivityById(updateActivityRequest.getId());
        if(user.getType()==0) throw new CommonException(CommonErrorCode.USER_NOT_ADMIN);
        if(user.getId()!=activity.getPublisherId()) throw new CommonException((CommonErrorCode.ADMIN_NOT_SAME));
        if(updateActivityRequest.getOrderEndTime().compareTo(TimeUtil.getCurrentTimestamp())<0) throw new CommonException(CommonErrorCode.ORDER_HAS_END);
        int status = 0;
        if(updateActivityRequest.getOrderStartTime().compareTo(TimeUtil.getCurrentTimestamp())>0){
            status = -2;
            new MemberThead(updateActivityRequest.getOrderStartTime(),0,updateActivityRequest.getId()).updateStatus();
        }
        new MemberThead(updateActivityRequest.getStartTime(),1,updateActivityRequest.getId()).updateStatus();
        new MemberThead(updateActivityRequest.getEndTime(),-1,updateActivityRequest.getId()).updateStatus();
        activityMapper.updateActivity(updateActivityRequest.getName(),status,updateActivityRequest.getOrganizerId(),updateActivityRequest.getStartTime(),updateActivityRequest.getEndTime(),updateActivityRequest.getPlace(), DatesUtil.hourDiff(updateActivityRequest.getStartTime(),updateActivityRequest.getEndTime()),updateActivityRequest.getOrderStartTime(),updateActivityRequest.getOrderEndTime(),0,updateActivityRequest.getIntroduction(),updateActivityRequest.getIsCheck());
    }


    @Override
    public Activity getActivityById(Long id){
        return activityMapper.getActivityById(id);
    }

    @Override
    public Page<BriefActivity> getBriefActivityByOrganizer(int pageSize,int pageNum,Long organizerId) {
        PageHelper.startPage(pageNum,pageSize,"start_time desc");
        return new Page<>(new PageInfo<>(activityMapper.getBriefActivityListByOrganizer(organizerId)));
    }

    @Override
    public Page<BriefActivity> getBriefActivityByPublisher(int pageSize, int pageNum, Long publisherId) {
        User user = userMapper.selectByPrimaryKey(publisherId);
        if(user.getType()==0) throw new CommonException(CommonErrorCode.USER_NOT_ADMIN);
        PageHelper.startPage(pageNum,pageSize,"start_time desc");
        return new Page<>(new PageInfo<>(activityMapper.getBriefActivityListByOrganizer(publisherId)));

    }


}
