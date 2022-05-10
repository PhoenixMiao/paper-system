package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.dto.BriefComment;
import com.phoenix.paper.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {
    @Update("UPDATE comment SET delete_time = #{delete_time} WHERE use_id = #{user_id};")
    void deleteComment(@Param("delete_time")String deleteTime, @Param("user_id")Long userId);

    @Update("UPDATE comment SET delete_time = #{delete_time} WHERE object_id = #{object_id} AND object_type = #{object_type};")
    void cancelComment(@Param("delete_time")String deleteTime, @Param("objectId")Long objectId, @Param("object_type")Integer object_type);

    @Select("SELECT * FROM comment where object_id=#{objectId} AND object_type=#{objectType} WHERE delete_time IS NULL")
    List<Comment> getCommentList( @Param("objectId")Long objectId, @Param("object_type")Integer object_type);

    List<Comment> getMultilevelCommentList( @Param("commentId")String commentId);
}
