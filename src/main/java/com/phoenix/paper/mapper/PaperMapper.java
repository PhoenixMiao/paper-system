package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.dto.BriefPaper;
import com.phoenix.paper.dto.PaperAndNoteData;
import com.phoenix.paper.entity.Paper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    @Select("SELECT paper_direction.direction_id as direction, count(*) as number FROM paper_direction LEFT JOIN paper ON paper.id=paper_direction.paper_id  WHERE uploader_id=#{userId} AND TO_DAYS(NOW())-TO_DAYS(upload_time)<=#{period} group by direction_id")
    List<PaperAndNoteData> getPaperData(@Param("userId")Long userId, @Param("period")Integer period);
}
