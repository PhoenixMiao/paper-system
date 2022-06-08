package com.phoenix.paper.service;

import com.phoenix.paper.controller.request.AddResearchDirectionRequest;
import com.phoenix.paper.dto.BriefNode;

import java.util.List;

public interface ResearchDirectionService {
    Long addResearchDirection(AddResearchDirectionRequest addResearchDirectionRequest,Long creatorId);

    List<BriefNode> getSons(Long father);

    List<Long> getAllSons(Long father);

    void deleteNode(Long id);

    void updateNode(Long id,String name);

    String getResearchDirectionName(Long id);
}
