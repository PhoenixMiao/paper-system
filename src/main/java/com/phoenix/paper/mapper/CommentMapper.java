package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.dto.BriefComment;
import com.phoenix.paper.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {
    @Select("SELECT * FROM comment WHERE note_id=#{objectId} AND delete_time IS NULL")
    List<BriefComment> getNoteCommentList( @Param("objectId")Long objectId);

    @Select("SELECT * FROM comment WHERE comment_id=#{objectId} AND delete_time IS NULL")
    List<BriefComment> getSecondLevelCommentList( @Param("objectId")Long objectId);
}
