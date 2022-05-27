package com.phoenix.paper.service;

import com.phoenix.paper.common.Page;
import com.phoenix.paper.controller.request.AddPaperRequest;
import com.phoenix.paper.controller.request.SearchPaperRequest;
import com.phoenix.paper.dto.BriefPaper;
import com.phoenix.paper.entity.Paper;
import org.springframework.web.multipart.MultipartFile;

public interface PaperService {

    Paper getPaperById(Long paperId);

    Page<BriefPaper> getPaperList(int pageNum, int pageSize, int orderBy);

    Long addPaper(Long userId, AddPaperRequest addPaperRequest);

    String uploadPaper(MultipartFile file, Long paperId);

    void deletePaper(Long paperId, Long userId);

    Page<BriefPaper> findPaperByTitle(Integer pageNum, Integer pageSize, String title);

    Long addPaperQuotation(Long quoterId, Long quotedId);

    Page<BriefPaper> searchPaperByDirection(int pageNum, int pageSize, int orderBy, SearchPaperRequest searchPaperRequest);


    //void addPaperIndex(String paperContents, Paper paper) throws IOException;
}
