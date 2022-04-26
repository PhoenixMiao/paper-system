package com.phoenix.paper.service;

import com.phoenix.paper.common.Page;
import com.phoenix.paper.dto.BriefPaper;
import com.phoenix.paper.entity.Paper;

public interface PaperService {

    Paper getPaperById(Long paperId);

    Page<BriefPaper> getPaperList(int pageNum, int pageSize, String orderBy);

    Page<BriefPaper> getUserPaperList(Integer pageNum,Integer pageSize,Long userId);
}
