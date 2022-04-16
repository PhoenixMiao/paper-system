package com.phoenix.read.mapper;

import com.phoenix.read.MyMapper;
import com.phoenix.read.dto.BriefUser;
import com.phoenix.read.entity.User;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yannis
 * @version 2020/11/7 9:17
 */

@Repository
public interface UserMapper extends MyMapper<User> {
    @Select("SELECT id,name,nickname,portrait FROM user")
    List<BriefUser> getBriefUser();

    @Update("UPDATE user SET type=#{type} WHERE id=#{id}")
    void toAdmin(@Param("type")Integer type,@Param("id")Long id);

    @Update("UPDATE user SET nickname=#{nickname},department=#{department},major=#{major},grade=#{grade},telephone=#{telephone},q_q=#{q_q},wechat_num=#{wechat_num},portrait=#{portrait} WHERE id=#{id};")
    void updateUser(@Param("nickname")String nickname,@Param("department")String department,@Param("major")String major,@Param("grade")String grade,@Param("telephone")String telephone,@Param("q_q")String QQ,@Param("wechat_num")String wechatNum,@Param("portrait")String portrait,@Param("id")Long id);

    @Update("UPDATE user SET is_mute=#{is_mute} WHERE id=#{id}")
    void muteUser(@Param("is_mute")Integer isMute,@Param("id")Long id);

    @Update("UPDATE user SET organizer_id=#{organizer_id} WHERE id=#{id}")
    void classifyUser(@Param("organizer_id")Long organizerId,@Param("id")Long id);
}
