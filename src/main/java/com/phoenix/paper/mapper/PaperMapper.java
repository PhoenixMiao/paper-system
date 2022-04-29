package com.phoenix.paper.mapper;

import com.phoenix.paper.MyMapper;
import com.phoenix.paper.dto.BriefPaper;
import com.phoenix.paper.entity.Paper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface PaperMapper extends MyMapper<Paper> {
    @Select("SELECT paper.id,title,nickname,publish_date,summary,link,like_number,collect_number FROM paper LEFT JOIN user ON paper.uploader_id=user.id WHERE user.delete_time IS NULL AND paper.delete_time IS NULL")
    List<BriefPaper> getPaperList();

    @Select("SELECT paper.id,title,nickname,publish_date,summary,link,like_number,collect_number FROM paper join user on paper.uploader_id=user.id where uploader_id=#{userId} and user.delete_time IS NULL AND paper.delete_time IS NULL")
    List<BriefPaper> getUserPaperList(@Param("userId")Long userId);

    @Select("SELECT like_number FROM paper where id=#{id}")
    Long getPaperLikes(@Param("id")Long id);

    @Update("UPDATE paper set like_number=#{likeNumber} where id=#{id}")
    void setPaperLikes(@Param("id")Long id,@Param("likeNumber")Long likeNumber);

    @Select("SELECT collect_number FROM paper where id=#{id}")
    Long getPaperCollects(@Param("id")Long id);

    @Update("UPDATE paper set collect_Number=#{collectNumber} where id=#{id}")
    void setPaperCollects(@Param("id")Long id,@Param("collectNumber")Long collectNumber);

    @Select("SELECT COUNT(*) FROM paper where uploader_id=#{userId}")
    Long getUserTotalPaperNumber(@Param("userId")Long userId);

    @Select("SELECT COUNT(*) FROM paper where uploader_id=#{userId} and DATEDIFF(curdate(),date_format(upload_time,'%y-%m-%d'))<=7")
    Long getUserPaperNumberInThisWeek(@Param("userId")Long userId);
}
