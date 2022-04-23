package com.phoenix.read.service.impl;

import com.phoenix.read.common.CommonErrorCode;
import com.phoenix.read.common.CommonException;
import com.phoenix.read.controller.request.AddResearchDirectionReuqest;
import com.phoenix.read.mapper.ResearchDirectionMapper;
import com.phoenix.read.mapper.UserMapper;
import com.phoenix.read.service.ResearchDirectionService;
import com.phoenix.read.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class ResearchDirectionServiceImpl implements ResearchDirectionService {

    @Autowired
    private ResearchDirectionMapper researchDirectionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TimeUtil timeUtil;

    @Override
    public void addResearchDirection(AddResearchDirectionReuqest addResearchDirectionReuqest,Long creatorId) throws DataAccessException{
        if(userMapper.getUserById(creatorId).getType()!=1)throw new CommonException(CommonErrorCode.USER_NOT_ADMIN);
        if(researchDirectionMapper.fingDirectionWithSameName(addResearchDirectionReuqest.getName())!=null)throw new CommonException(CommonErrorCode.REPETITIVE_DIRECTION);
        researchDirectionMapper.addResearchDirection(addResearchDirectionReuqest.getName(), addResearchDirectionReuqest.getRootId(), addResearchDirectionReuqest.getFatherId(), addResearchDirectionReuqest.getTreeId(), addResearchDirectionReuqest.getHeight(), addResearchDirectionReuqest.getLayer_id(), addResearchDirectionReuqest.getPath(), creatorId, timeUtil.getCurrentTimestamp(), addResearchDirectionReuqest.getDeleteTime());
    }
}
