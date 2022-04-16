package com.phoenix.read.mapper;

import com.phoenix.read.MyMapper;
import com.phoenix.read.dto.BriefPassage;
import com.phoenix.read.entity.Comment;
import com.phoenix.read.entity.Organizer;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMapper extends MyMapper<Comment> {
    @Select("SELECT id FROM comment WHERE object_id=#{object_id} AND object_type=#{object_type}")
    List<Long> getCommentList(@Param("object_id") Long objectId, @Param("object_type")Integer objectType);

    @Insert("INSERT INTO comment(object_id,object_type,user_id,comment_time,comment) VALUES (#{object_id},#{object_type},#{user_id},#{comment_time},#{comment}")
    Long addComment(@Param("object_id")Long objectId,
                    @Param("object_type") Integer objectType,
                    @Param("user_id") Long userId,
                    @Param("comment_time") String commentTime,
                    @Param("comment") String comment
                    );

}
