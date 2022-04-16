package com.phoenix.read.mapper;

import com.phoenix.read.MyMapper;
import com.phoenix.read.dto.BriefPassage;
import com.phoenix.read.entity.Organizer;
import com.phoenix.read.entity.Passage;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassageMapper extends MyMapper<Passage> {
    @Select("SELECT * FROM passage LEFT JOIN (SELECT COUNT(*) AS times,object_id FROM comment WHERE object_type = 0 GROUP BY object_id)tmp ON passage.id = tmp.object_id WHERE type=#{type} AND subtype=#{subtype} ORDER BY times DESC")
    List<Passage> getPassageListByHot(@Param("type") Integer type, @Param("subtype")Integer subtype);

    @Select("SELECT * FROM passage WHERE type=#{type} AND subtype=#{subtype} ORDER BY publish_time DESC")
    List<Passage> getPassageListByTime(@Param("type") Integer type, @Param("subtype")Integer subtype);

}

