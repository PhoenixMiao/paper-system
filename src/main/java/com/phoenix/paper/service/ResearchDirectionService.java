package com.phoenix.paper.service;

import com.phoenix.paper.controller.request.AddResearchDirectionRequest;

public interface ResearchDirectionService {
    Long addResearchDirection(AddResearchDirectionRequest addResearchDirectionReuqest,Long creatorId);
}
