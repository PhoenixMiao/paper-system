package com.phoenix.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResearchDirectionServiceImpl implements ResearchDirectionService {

    @Autowired
    private ResearchDirectionMapper researchDirectionMapper;

    @Override
    public Long addResearchDirection(AddResearchDirectionRequest addResearchDirectionRequest, Long creatorId) throws CommonException {
        QueryWrapper<ResearchDirection> researchDirectionQueryWrapper = new QueryWrapper<>();
        researchDirectionQueryWrapper.eq("name",addResearchDirectionRequest.getName());
        ResearchDirection researchDirectionOld = researchDirectionMapper.selectOne(researchDirectionQueryWrapper);
        if (researchDirectionOld == null || researchDirectionOld.getDeleteTime()!=null)
            throw new CommonException(CommonErrorCode.REPETITIVE_DIRECTION);
        ResearchDirection researchDirection;
        if (addResearchDirectionRequest.getFatherId() != 0) {
            ResearchDirection fatherResearchDirection = researchDirectionMapper.selectById(addResearchDirectionRequest.getFatherId());
            if(fatherResearchDirection.getIsLeaf()==1){
                fatherResearchDirection.setIsLeaf(0);
                if(researchDirectionMapper.updateById(fatherResearchDirection)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
            }
            synchronized (this) {
                QueryWrapper<ResearchDirection> researchDirectionQueryWrapper1 = new QueryWrapper<>();
                researchDirectionQueryWrapper1.eq("root_id",fatherResearchDirection.getRootId());
                researchDirectionQueryWrapper1.eq("father_id",fatherResearchDirection.getFatherId());
                int nodeNum = researchDirectionMapper.selectCount(researchDirectionQueryWrapper1);
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
                researchDirection = ResearchDirection.builder()
                        .name(addResearchDirectionRequest.getName())
                        .path("0")
                        .createTime(TimeUtil.getCurrentTimestamp())
                        .creatorId(creatorId)
                        .fatherId((long) 0)
                        .isLeaf(1)
                        .build();
                researchDirectionMapper.insert(researchDirection);
                researchDirection.setRootId(researchDirection.getRootId());
                if(researchDirectionMapper.updateById(researchDirection)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
            }
        }
        return researchDirection.getId();
    }

    @Override
    public List<BriefNode> getSons(Long father) throws CommonException{
        if(father==0) return researchDirectionMapper.getSons((long)0);
        ResearchDirection fatherDirection = researchDirectionMapper.selectById(father);
        if(fatherDirection == null || fatherDirection.getDeleteTime()!=null) throw new CommonException(CommonErrorCode.RESEARCH_DIRECTION_NOT_EXIST);
        if(fatherDirection.getIsLeaf()==1) throw new CommonException(CommonErrorCode.HAVE_NO_SON);
        return researchDirectionMapper.getSons(father);
    }

    @Override
    public List<Long> getAllSons(Long father) throws CommonException{
        ResearchDirection researchDirection = researchDirectionMapper.selectById(father);
        if(researchDirection == null || researchDirection.getDeleteTime()!=null) throw new CommonException(CommonErrorCode.RESEARCH_DIRECTION_NOT_EXIST);
        return researchDirectionMapper.getAllSons(researchDirection.getRootId(),researchDirection.getPath()+'%');
    }

    @Override
    public void deleteNode(Long id) throws CommonException{
        List<Long> sonList = getAllSons(id);
        String deleteTime = TimeUtil.getCurrentTimestamp();
        for(Long node:sonList){
            ResearchDirection researchDirection = researchDirectionMapper.selectById(node);
            researchDirection.setDeleteTime(TimeUtil.getCurrentTimestamp());
            if(researchDirectionMapper.updateById(researchDirection)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
            //todo 删除该研究方向所有论文以及论文的所有依附品
        }
    }

    @Override
    public void updateNode(Long id,String name) throws CommonException{
        ResearchDirection researchDirection = researchDirectionMapper.selectById(id);
        if(researchDirection==null || researchDirection.getDeleteTime() != null) throw new CommonException(CommonErrorCode.RESEARCH_DIRECTION_NOT_EXIST);
        researchDirection.setName(name);
        if(researchDirectionMapper.updateById(researchDirection)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
    }
}
