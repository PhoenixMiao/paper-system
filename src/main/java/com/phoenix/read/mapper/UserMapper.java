package com.phoenix.read.mapper;

import com.phoenix.read.MyMapper;
import com.phoenix.read.dto.BriefUser;
import com.phoenix.read.entity.User;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
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
    @Select("SELECT id,name,nickname,portrait,type FROM user")
    List<BriefUser> getBriefUserList();

    @Select("SELECT * from user where id=#{id}")
    User getUserById(@Param("id")Long id);

    @Update("UPDATE user SET type=#{type} WHERE id=#{id}")
    void toAdmin(@Param("type")Integer type,@Param("id")Long id);

    @Update("UPDATE user SET nickname=#{nickname},name=#{name},gender=#{gender},grade=#{grade},school=#{school},major=#{major},grade=#{grade}, email=#{email}, telephone=#{telephone},portrait=#{portrait}, type=#{type} WHERE id=#{id};")
    void updateUser(@Param("nickname")String nickname, @Param("name")String name, @Param("gender") Integer gender, @Param("school")String school, @Param("major")String major, @Param("grade")String grade, @Param("email")String email, @Param("telephone")String telephone,@Param("poatrait")String poatrait, @Param("type")Integer type, @Param("id")Long id);

    @Update("UPDATE user SET is_mute=#{is_mute} WHERE id=#{id}")
    void muteUser(@Param("is_mute")Integer isMute,@Param("id")Long id);

    @Update("UPDATE user SET organizer_id=#{organizer_id} WHERE id=#{id}")
    void classifyUser(@Param("organizer_id")Long organizerId,@Param("id")Long id);

    @Select("SELECT * FROM user WHERE account_num = #{account_num}")
    User getUserByNum(@Param("account_num")String number);
}
