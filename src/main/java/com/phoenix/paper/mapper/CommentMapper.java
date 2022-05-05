package com.phoenix.paper.mapper;

import com.phoenix.paper.MyMapper;
import com.phoenix.paper.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface CommentMapper extends MyMapper<Comment> {
    @Update("UPDATE comment SET delete_time = #{delete_time} WHERE use_id = #{user_id};")
    void deleteComment(@Param("delete_time")String deleteTime, @Param("user_id")Long userId);
}
