package com.phoenix.read.service;

import com.phoenix.read.common.Page;
import com.phoenix.read.controller.request.GetPassageListRequest;
import com.phoenix.read.controller.request.PostRequest;
import com.phoenix.read.controller.request.SearchRequest;
import com.phoenix.read.controller.response.GetPassageResponse;
import com.phoenix.read.dto.BriefPassage;
import com.phoenix.read.entity.Organizer;
import com.phoenix.read.entity.Passage;

import java.util.List;

public interface PassageService {

    Page<Passage> getPassageList(GetPassageListRequest getPassageListRequest);

    GetPassageResponse getPassageById(Long passageId);

    Long postNewPassage(PostRequest postRequest,Long userId);

    Page<BriefPassage> searchPassage(SearchRequest searchRequest);
}
