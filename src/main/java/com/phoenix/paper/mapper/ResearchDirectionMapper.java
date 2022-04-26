package com.phoenix.paper.mapper;

import com.phoenix.paper.MyMapper;
import com.phoenix.paper.dto.BriefNode;
import com.phoenix.paper.entity.ResearchDirection;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ResearchDirectionMapper extends MyMapper<ResearchDirection> {
    @Select("SELECT id,name,is_leaf FROM research_direction WHERE father_id = #{father_id};")
    List<BriefNode> getSons(@Param("father_id")Long father_id);

    @Select("SELECT id FROM research_direction WHERE root_id = #{root_id} AND path LIKE #{path};")
    List<Long> getAllSons(@Param("root_id")Long root_id,@Param("path")String path);
}
