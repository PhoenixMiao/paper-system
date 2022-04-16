package com.phoenix.read.service;

import com.phoenix.read.common.Page;
import com.phoenix.read.controller.request.UpdateActivityRequest;
import com.phoenix.read.dto.BriefActivity;
import com.phoenix.read.dto.BriefPush;
import com.phoenix.read.entity.Activity;

import java.util.List;

public interface ActivityService {
    void updateActivity(UpdateActivityRequest updateActivityRequest, Long userId);

    Activity getActivityById(Long id);

    Page<BriefActivity> getBriefActivityByOrganizer(int pageSize,int pageNum,Long organizerId);

    Page<BriefActivity> getBriefActivityByPublisher(int pageSize,int pageNum,Long publisherId);
}
