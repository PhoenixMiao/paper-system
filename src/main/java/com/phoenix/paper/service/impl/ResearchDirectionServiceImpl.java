package com.phoenix.paper.service.impl;

import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.controller.request.AddResearchDirectionRequest;
import com.phoenix.paper.dto.BriefNode;
import com.phoenix.paper.entity.ResearchDirection;
import com.phoenix.paper.mapper.ResearchDirectionMapper;
import com.phoenix.paper.service.ResearchDirectionService;
import com.phoenix.paper.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResearchDirectionServiceImpl implements ResearchDirectionService {

    @Autowired
    private ResearchDirectionMapper researchDirectionMapper;

    @Override
    public Long addResearchDirection(AddResearchDirectionRequest addResearchDirectionRequest, Long creatorId) throws CommonException {
        if (researchDirectionMapper.selectOne(ResearchDirection.builder().name(addResearchDirectionRequest.getName()).build()) != null)
            throw new CommonException(CommonErrorCode.REPETITIVE_DIRECTION);
        ResearchDirection researchDirection;
        if (addResearchDirectionRequest.getFatherId() != 0) {
            ResearchDirection fatherResearchDirection = researchDirectionMapper.selectByPrimaryKey(addResearchDirectionRequest.getFatherId());
            if(fatherResearchDirection.getIsLeaf()==1) researchDirectionMapper.updateByPrimaryKeySelective(ResearchDirection.builder().id(fatherResearchDirection.getId()).isLeaf(0).build());
            synchronized (this) {
                int nodeNum = researchDirectionMapper.selectCount(ResearchDirection.builder().rootId(fatherResearchDirection.getRootId()).fatherId(fatherResearchDirection.getFatherId()).build());
                researchDirection = ResearchDirection.builder()
                        .fatherId(fatherResearchDirection.getId())
                        .createTime(TimeUtil.getCurrentTimestamp())
                        .creatorId(creatorId)
                        .name(addResearchDirectionRequest.getName())
                        .rootId(fatherResearchDirection.getRootId())
                        .path(fatherResearchDirection.getPath() + (new Integer(nodeNum)).toString())
                        .isLeaf(1)
                        .build();
                researchDirectionMapper.insert(researchDirection);
            }
        } else {
            synchronized (this) {
                int num = researchDirectionMapper.select(ResearchDirection.builder().fatherId((long)0).build()).size();
                researchDirection = ResearchDirection.builder()
                        .name(addResearchDirectionRequest.getName())
                        .path("0")
                        .createTime(TimeUtil.getCurrentTimestamp())
                        .creatorId(creatorId)
                        .fatherId((long) 0)
                        .isLeaf(1)
                        .build();
                researchDirectionMapper.insert(researchDirection);
                researchDirectionMapper.updateByPrimaryKeySelective(ResearchDirection.builder().id(researchDirection.getId()).rootId(researchDirection.getId()).build());
            }
        }
        return researchDirection.getId();
    }

    @Override
    public List<BriefNode> getSons(Long father) throws CommonException{
        if(father==0) return researchDirectionMapper.getSons((long)0);
        if(researchDirectionMapper.selectByPrimaryKey(father).getIsLeaf()==1) throw new CommonException(CommonErrorCode.HAVE_NO_SON);
        return researchDirectionMapper.getSons(father);
    }

    @Override
    public List<Long> getAllSons(Long father) throws CommonException{
        ResearchDirection researchDirection = researchDirectionMapper.selectByPrimaryKey(father);
        return researchDirectionMapper.getAllSons(researchDirection.getRootId(),researchDirection.getPath()+'%');
    }
}
