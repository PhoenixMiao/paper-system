package com.phoenix.paper.mapper;

import com.phoenix.paper.MyMapper;
import com.phoenix.paper.entity.Collection;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface CollectionMapper extends MyMapper<Collection> {

    @Update("UPDATE collection SET delete_time=#{delete_time} where object_id=#{object_id} and object_type=#{object_type}")
    void cancelCollect(@Param("delete_time")String delete_time, @Param("object_id")long object_id, @Param("object_type")Integer object_type);
}
