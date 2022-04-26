package com.phoenix.paper.mapper;

import com.phoenix.paper.MyMapper;
import com.phoenix.paper.dto.BriefPaper;
import com.phoenix.paper.entity.Paper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface PaperMapper extends MyMapper<Paper> {
    @Select("SELECT id,title,author,publish_date,summary,link,like_number,collect_number FROM paper")
    List<BriefPaper> getPaperList();

    @Select("SELECT id,title,author,publish_date,summary,link,like_number,collect_number FROM paper where uploader_id=#{userId}")
    List<BriefPaper> getUserPaperList(@Param("userId")Long userId);

    @Select("SELECT like_number FROM paper where id=#{id}")
    Long getPaperLikes(@Param("id")Long id);

    @Update("UPDATE paper set like_number=#{likeNumber} where id=#{id}")
    void setPaperLikes(@Param("id")Long id,@Param("like_number")Long like_number);
}
