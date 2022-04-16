package com.phoenix.read.mapper;

import com.phoenix.read.MyMapper;
import com.phoenix.read.dto.BriefActivity;
import com.phoenix.read.entity.Activity;
import com.phoenix.read.entity.Push;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityMapper extends MyMapper<Activity> {
    @Insert("INSERT INTO activity(type,publisher_id) VALUES (#{type},#{publisher_id});")
    Long newActivity(@Param("type") Integer type, @Param("publisher_id") Long publisherId);

    @Update("UPDATE activity SET name=#{name},status=#{status},organizer_id=#{organizer_id},start_time=#{start_time},end_time=#{end_time},place=#{place},time=#{time},order_start_time=#{order_start_time},order_end_time=#{order_end_time},people=#{people},introduction=#{introduction},is_check=#{is_check} WHERE id=#{id};")
    void updateActivity(@Param("name") String name, @Param("status") Integer status, @Param("organizer_id") Long organizerId, @Param("start_time") String startTime, @Param("end_time") String endTime, @Param("place") String place, @Param("time") Integer time, @Param("order_start_time") String orderStartTime, @Param("order_end_time") String orderEndTime,
                        @Param("people") Integer people, @Param("introduction") String introduction, @Param("is_check") Integer isCheck);


    @Select("SELECT * FROM activity WHERE id=#{id}")
    Activity getActivityById(@Param("id") Long id);

    @Update("UPDATE activity SET status=#{status} WHERE id=#{id};")
    void updateStatus(@Param("status") Integer status, @Param("id") Long id);

    @Select("SELECT id,start_time,name FROM activity WHERE organizer_id=#{organizer_id}")
    List<BriefActivity> getBriefActivityListByOrganizer(@Param("organizer_id") Long organizerId);

    @Update("UPDATE activity SET people=#{people} WHERE id=#{id}")
    void makeOrder(@Param("people") Integer people, @Param("id") Long id);

    @Select("SELECT id,start_time,name FROM activity WHERE publisher_id=#{publisher_id}")
    List<BriefActivity> getBriefActivityListByPublisherId(@Param("publisher_id") Long publisherId);

    @Select("SELECT * FROM activity ORDER BY people desc")
    List<Activity> getNotEndActivity();

    @Select("SELECT id FROM activity WHERE publisher_id=#{userId} OR organizerId=#{user_id}")
    List<Long> getRelevantActivityIdByUser(@Param("user_id")Long userId);
}
