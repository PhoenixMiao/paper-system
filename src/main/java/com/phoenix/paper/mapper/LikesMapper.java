package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.entity.Likes;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface LikesMapper extends BaseMapper<Likes> {

    @Update("UPDATE likes SET delete_time=#{delete_time} where object_id=#{object_id} and object_type=#{object_type}")
    void cancelLike(@Param("delete_time")String delete_time, @Param("object_id")long object_id, @Param("object_type")Integer object_type);

    @Update("UPDATE likes SET delete_time = #{delete_time} WHERE use_id = #{user_id};")
    void deleteLike(@Param("delete_time")String deleteTime,@Param("user_id")Long userId);
}
