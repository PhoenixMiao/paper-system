package com.phoenix.read.mapper;

import com.phoenix.read.MyMapper;
import com.phoenix.read.entity.Member;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberMapper extends MyMapper<Member> {
    @Insert("INSERT INTO member(activity_id,user_id,create_time,status) VALUES(#{activity_id},#{user_id},#{create_time},#{status});")
    Long makeOrder(@Param("activity_id")Long activityId,@Param("user_id")Long userId,@Param("create_time")String createTime,@Param("status")Integer status);

    @Update("UPDATE member SET status=#{status} WHERE id=#{id}")
    void updateStatus(@Param("status")Integer status,@Param("id")Long id);

    @Select("SELECT * FROM member WHERE activity_id=#{activity_id} AND status=#{status};")
    List<Member> getUncommittedMemberList(@Param("activity_id")Long activityId,@Param("status")Integer status);

    @Select("SELECT * FROM member WHERE user_id=#{user_id}")
    List<Member> getMemberListByUserId(@Param("user_id")Long userId);

    @Select("SELECT * FROM member WHERE user_id=#{user_id} AND activity_id=#{activity_id} LIMIT 1")
    Member getMemberByUserIdAndActivityId(@Param("user_id")Long userId,@Param("activity_id")Long activityId);

    @Select("SELECT * FROM member WHERE activity_id=#{activity_id}")
    List<Member> getMemberListByActivityId(@Param("activity_id")Long activityId);
}
