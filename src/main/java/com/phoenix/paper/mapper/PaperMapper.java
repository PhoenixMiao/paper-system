package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.dto.BriefPaper;
import com.phoenix.paper.entity.Paper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface PaperMapper extends BaseMapper<Paper> {
    @Select("SELECT id,title,publish_date,summary,link,like_number,collect_number FROM paper WHERE paper.delete_time IS NULL")
    List<BriefPaper> getPaperList();

    @Select("SELECT id,title,publish_date,summary,link,like_number,collect_number FROM paper WHERE uploader_id=#{userId} AND paper.delete_time IS NULL")
    List<BriefPaper> getUserPaperList(@Param("userId")Long userId);

    @Select("SELECT like_number FROM paper WHERE id=#{id}")
    Long getPaperLikes(@Param("id")Long id);

    @Select("SELECT collect_number FROM paper where id=#{id}")
    Long getPaperCollects(@Param("id")Long id);

    @Select("SELECT COUNT(*) FROM paper where uploader_id=#{userId}")
    Long getUserTotalPaperNumber(@Param("userId")Long userId);

    @Select("SELECT COUNT(*) FROM paper where uploader_id=#{userId} and DATEDIFF(curdate(),date_format(upload_time,'%y-%m-%d'))<=7")
    Long getUserPaperNumberInThisWeek(@Param("userId")Long userId);
}
