package com.phoenix.paper.service;

import com.phoenix.paper.common.Page;
import com.phoenix.paper.controller.request.AddPaperRequest;
import com.phoenix.paper.controller.response.GetUserPaperListResponse;
import com.phoenix.paper.dto.BriefPaper;
import com.phoenix.paper.entity.Paper;
import org.springframework.web.multipart.MultipartFile;

public interface PaperService {

    Paper getPaperById(Long paperId);

    Page<BriefPaper> getPaperList(int pageNum, int pageSize, int orderBy);

    GetUserPaperListResponse getUserPaperList(Integer pageNum, Integer pageSize, Long userId);

    Long addPaper(Long userId);

    String uploadPaper(MultipartFile file, Long paperId);
}
