package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.dto.BriefComment;
import com.phoenix.paper.entity.Collection;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CollectionMapper extends BaseMapper<Collection>{

    @Select("SELECT * FROM collection WHERE object_id=#{objectId} AND object_type=#{objectType} AND delete_time IS NULL")
    Collection getCollect(@Param("objectId")Long objectId,@Param("objectType") Integer objectType);

}
