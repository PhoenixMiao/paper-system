package com.phoenix.paper.service;

import com.phoenix.paper.common.Page;
import com.phoenix.paper.controller.request.AddPaperRequest;
import com.phoenix.paper.controller.request.SearchPaperRequest;
import com.phoenix.paper.controller.request.UpdatePaperRequest;
import com.phoenix.paper.dto.BriefPaper;
import com.phoenix.paper.dto.DetailedPaper;
import com.phoenix.paper.entity.Paper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface PaperService {

    DetailedPaper getPaperById(Long paperId);

    Page<BriefPaper> getPaperList(int pageNum, int pageSize, int orderBy);

    Long addPaper(Long userId, AddPaperRequest addPaperRequest);

    String uploadPaper(MultipartFile file, Long paperId);

    void deletePaper(Long paperId, Long userId);

    Page<BriefPaper> findPaperByTitle(Integer pageNum, Integer pageSize, String title);

    Long addPaperQuotation(Long quoterId, Long quotedId);

    Page<BriefPaper> searchPaperByDirection(int pageNum, int pageSize, int orderBy, SearchPaperRequest searchPaperRequest);

    List<Map<String, Object>> searchPaper(String contents, int pageNum, int pageSize);

    void updatePaper(Long paperId, Long userId, UpdatePaperRequest updatePaperRequest);

    Long addQuotation(Long quoterId, Long quotedId, String remarks);

    List<Paper> searchPaperBefore(Long quoterId, String title);

    void updateQuotation(Long id, String remarks);

    void deleteQuotation(Long id);

    Long addDirection(Long paperId, Long directionId);

    void deleteDirection(Long paperDirectionId);
}
