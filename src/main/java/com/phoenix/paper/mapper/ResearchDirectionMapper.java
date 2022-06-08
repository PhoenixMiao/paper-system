package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.dto.BriefNode;
import com.phoenix.paper.entity.ResearchDirection;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ResearchDirectionMapper extends BaseMapper<ResearchDirection> {
    @Select("SELECT id,name,father_id,is_leaf FROM research_direction WHERE father_id = #{father_id} AND delete_time IS NULL;")
    List<BriefNode> getSons(@Param("father_id") Long father_id);

    @Select("SELECT id FROM research_direction WHERE root_id = #{root_id} AND path LIKE #{path} AND LEN(delete_time) = 0 AND delete_time IS NULL;")
    List<Long> getAllSons(@Param("root_id") Long root_id, @Param("path") String path);

    @Select("SELECT id,name,father_id,is_leaf FROM research_direction;")
    List<BriefNode> getAll();
}
