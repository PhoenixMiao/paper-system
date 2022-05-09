package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.dto.BriefCollection;
import com.phoenix.paper.entity.Collection;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CollectionMapper extends BaseMapper<Collection>{

    @Update("UPDATE collection SET delete_time=#{delete_time} where object_id=#{object_id} and object_type=#{object_type}")
    void cancelCollect(@Param("delete_time")String delete_time, @Param("object_id")long object_id, @Param("object_type")Integer object_type);

    @Update("UPDATE collection SET delete_time = #{delete_time} WHERE user_id = #{user_id};")
    void deleteCollect(@Param("delete_time")String delete_time,@Param("user_id")Long user_id);

    @Select("SELECT * FROM collection WHERE user_id=#{userId} and delete_time IS NULL")
    List<BriefCollection> getCollectionList(@Param("userId")Long userId);
}
