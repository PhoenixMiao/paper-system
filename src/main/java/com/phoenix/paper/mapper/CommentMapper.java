package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {
    @Select("SELECT * FROM comment WHERE object_id=#{objectId} AND object_type=#{objectType} AND delete_time IS NULL")
    List<Comment> getCommentList( @Param("objectId")Long objectId, @Param("object_type")Integer object_type);

    @Select("SELECT * FROM comment WHERE object_id=#{objectId} AND object_type=#{objectType} AND delete_time IS NULL AND FIND_IN_SET(ID,{idString})")
    List<Comment> getMultilevelCommentList(@Param("idString")String idString);
}
