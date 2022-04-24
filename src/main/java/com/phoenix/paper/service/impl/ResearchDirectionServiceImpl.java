package com.phoenix.paper.service.impl;

import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.controller.request.AddResearchDirectionRequest;
import com.phoenix.paper.entity.ResearchDirection;
import com.phoenix.paper.mapper.ResearchDirectionMapper;
import com.phoenix.paper.mapper.UserMapper;
import com.phoenix.paper.service.ResearchDirectionService;
import com.phoenix.paper.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class ResearchDirectionServiceImpl implements ResearchDirectionService {

    @Autowired
    private ResearchDirectionMapper researchDirectionMapper;

    @Override
    public Long addResearchDirection(AddResearchDirectionRequest addResearchDirectionRequest, Long creatorId) throws DataAccessException {
        if (researchDirectionMapper.selectOne(ResearchDirection.builder().name(addResearchDirectionRequest.getName()).build()) != null)
            throw new CommonException(CommonErrorCode.REPETITIVE_DIRECTION);
        ResearchDirection researchDirection;
        if (addResearchDirectionRequest.getFatherId() != 0) {
            ResearchDirection fatherResearchDirection = researchDirectionMapper.selectByPrimaryKey(addResearchDirectionRequest.getFatherId());
            synchronized (this) {
                int nodeNum = researchDirectionMapper.selectCount(ResearchDirection.builder().treeId(fatherResearchDirection.getTreeId()).height(fatherResearchDirection.getHeight() + 1).build());
                researchDirection = ResearchDirection.builder()
                        .fatherId(fatherResearchDirection.getId())
                        .createTime(TimeUtil.getCurrentTimestamp())
                        .creatorId(creatorId)
                        .height(fatherResearchDirection.getHeight() + 1)
                        .layerId((long) (nodeNum + 1))
                        .name(addResearchDirectionRequest.getName())
                        .rootId(fatherResearchDirection.getRootId())
                        .treeId(fatherResearchDirection.getTreeId())
                        .path(fatherResearchDirection.getPath() + (new Integer(nodeNum + 1)).toString())
                        .build();
                researchDirectionMapper.insert(researchDirection);
            }
        } else {
            synchronized (this) {
                int num = researchDirectionMapper.select(ResearchDirection.builder().height(0).build()).size();
                researchDirection = ResearchDirection.builder()
                        .treeId((long) (num + 1))
                        .name(addResearchDirectionRequest.getName())
                        .layerId((long) 0)
                        .path("0")
                        .height(0)
                        .createTime(TimeUtil.getCurrentTimestamp())
                        .creatorId(creatorId)
                        .fatherId((long) 0)
                        .build();
                researchDirectionMapper.insert(researchDirection);
                researchDirectionMapper.updateByPrimaryKeySelective(ResearchDirection.builder().id(researchDirection.getId()).rootId(researchDirection.getId()).build());
            }
        }
        return researchDirection.getId();
    }
}
