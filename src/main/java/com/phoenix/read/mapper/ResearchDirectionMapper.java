package com.phoenix.read.mapper;

import com.phoenix.read.entity.ResearchDirection;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ResearchDirectionMapper {
    @Select("SELECT * FROM research_direction where id=#{id}")
    ResearchDirection getResearchDerictionById(@Param("id")Long id);

    @Insert("INSERT into research_direction values(null,#{name},#{root_id},#{father_id},#{tree_id},#{height},#{layer_id},#{path},#{creator_id},#{create_time},#{delete_time})")
    void addResearchDirection(@Param("name")String name,
                              @Param("root_id")Long root_id,
                              @Param("father_id")Long father_id,
                              @Param("tree_id")Long tree_id,
                              @Param("height")Integer height,
                              @Param("layer_id")Long layer_id,
                              @Param("path")String path,
                              @Param("creator_id")Long creator_id,
                              @Param("create_time")String create_time,
                              @Param("delete_time")String delete_time);

    @Select("SElECT * FROM research_direction where name = #{name}")
    ResearchDirection fingDirectionWithSameName(@Param("name")String name);
}
