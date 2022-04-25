package com.phoenix.paper.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.paper.common.Page;
import com.phoenix.paper.dto.BriefPaper;
import com.phoenix.paper.entity.Paper;
import com.phoenix.paper.mapper.PaperMapper;
import com.phoenix.paper.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaperServiceImpl implements PaperService {

    @Autowired
    private PaperMapper paperMapper;

    @Override
    public Paper getPaperById(Long paperId){
        return paperMapper.selectByPrimaryKey(paperId);
    }

    @Override
    public Page<BriefPaper> getPaperList(int pageNum, int pageSize, String orderBy){
        PageHelper.startPage(pageNum,pageSize,orderBy);
        return new Page<>(new PageInfo<>(paperMapper.getPaperList()));
    }
}
