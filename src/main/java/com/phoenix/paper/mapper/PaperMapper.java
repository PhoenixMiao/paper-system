package com.phoenix.paper.mapper;

import com.phoenix.paper.MyMapper;
import com.phoenix.paper.dto.BriefPaper;
import com.phoenix.paper.entity.Paper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PaperMapper extends MyMapper<Paper> {
    @Select("SELECT id,title,author,publishDate,summary,link FROM paper")
    List<BriefPaper> getPaperList();
}
