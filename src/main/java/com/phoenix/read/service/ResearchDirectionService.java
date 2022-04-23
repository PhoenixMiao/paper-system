package com.phoenix.read.service;

import com.phoenix.read.controller.request.AddResearchDirectionReuqest;

public interface ResearchDirectionService {
    void addResearchDirection(AddResearchDirectionReuqest addResearchDirectionReuqest,Long creatorId);
}
