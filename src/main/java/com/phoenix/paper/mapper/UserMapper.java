package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.dto.BriefUser;
import com.phoenix.paper.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author yannis
 * @version 2020/11/7 9:17
 */

public interface UserMapper extends BaseMapper<User>{
    @Select("SELECT id,name,nickname,portrait,type FROM user WHERE user.delete_time IS NULL")
    List<BriefUser> getBriefUserList();

    @Select("SELECT * FROM user WHERE account_num = #{account_num}")
    User getUserByNum(@Param("account_num")String number);

    @Select("SELECT Id FROM user")
    List<Long> allUserIdList();

}
