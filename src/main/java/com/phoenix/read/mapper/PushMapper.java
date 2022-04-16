package com.phoenix.read.mapper;

import com.phoenix.read.MyMapper;
import com.phoenix.read.dto.BriefPush;
import com.phoenix.read.entity.Push;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PushMapper extends MyMapper<Push> {
    @Insert("INSERT INTO push(title,activityId,publish_date,content,type,picture,source,activity_type) VALUES (#{title},#{activity_id},#{publish_date},#{content},#{type},#{picture},#{source},#{activity_type});")
    Long newPush(@Param("title")String title,@Param("activity_id")Long activityId,@Param("publish_date")String publishDate,@Param("content")String content,@Param("type")Integer type,@Param("picture")String picture,@Param("source")String source,@Param("activity_type")Integer activityType);

    @Select("SELECT id,title,picture,type,activity_id FROM push WHERE activity_type=#{activity_type}")
    List<BriefPush> getPushList(@Param("activity_type")Integer activityType);

    @Select("SELECT id,title,picture,type,activity_id FROM push WHERE activity_id=#{activity_id}")
    List<BriefPush> getPushByActivityId(@Param("activity_id")Long activityId);

    @Select("SELECT * FROM push WHERE activity_id=#{activity_id}")
    List<Push> getPushListByActivityId(@Param("activity_id")Long activityId);
}
