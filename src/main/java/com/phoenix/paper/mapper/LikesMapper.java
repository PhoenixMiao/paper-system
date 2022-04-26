package com.phoenix.paper.mapper;

import com.phoenix.paper.MyMapper;
import com.phoenix.paper.entity.Likes;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface LikesMapper extends MyMapper<Likes> {

    @Update("UPDATE likes SET delete_time=#{delete_time} where object_id=#{object_id} and object_type=#{object_type}")
    void cancelLike(@Param("delete_time")String delete_time, @Param("object_id")long object_id, @Param("object_type")Integer object_type);
}
