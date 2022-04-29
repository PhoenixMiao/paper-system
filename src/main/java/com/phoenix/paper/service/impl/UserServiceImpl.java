package com.phoenix.paper.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.paper.common.*;
import com.phoenix.paper.config.YmlConfig;
import com.phoenix.paper.controller.request.UpdateUserRequest;
import com.phoenix.paper.controller.response.LoginResponse;
import com.phoenix.paper.dto.BriefUser;
import com.phoenix.paper.dto.SessionData;
import com.phoenix.paper.entity.User;
import com.phoenix.paper.mapper.UserMapper;
import com.phoenix.paper.service.UserService;
import com.phoenix.paper.util.*;
import com.phoenix.paper.util.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private YmlConfig ymlConfig;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PasswordUtil passwordUtil;

    @Override
    public LoginResponse login(String number, String password) throws CommonException{

        String sessionId = sessionUtils.generateSessionId();

        User user = userMapper.getUserByNum(number);

        AssertUtil.notNull(user,CommonErrorCode.USER_NOT_EXIST);

        AssertUtil.isNull(user.getDeleteTime(),CommonErrorCode.USER_NOT_EXIST);

        AssertUtil.isTrue(passwordUtil.convert(password).equals(user.getPassword()),CommonErrorCode.PASSWORD_NOT_RIGHT);

        sessionUtils.setSessionId(sessionId);
        redisUtils.set(sessionId,new SessionData(user),1440);
        return new LoginResponse(new SessionData(user),sessionId);
    }

    @Override
    public Page<BriefUser> getBriefUserList(int pageSize, int pageNum,Long userId) {
        if(userMapper.getUserById(userId).getType()!=1) throw new CommonException(CommonErrorCode.USER_NOT_SUPERADMIN);
        PageHelper.startPage(pageNum,pageSize,"create_time desc");
        //List<BriefUser> briefUsers = briefUserList.stream().parallel().filter(user -> user.getDeleteTime() == null).collect(Collectors.toList());
        return new Page<>(new PageInfo<>(userMapper.getBriefUserList()));
    }

//    @Override
//    public void toAdmin(Long userId,Long adminId) {
//        if(userMapper.selectByPrimaryKey(adminId).getType()!=2) throw new CommonException(CommonErrorCode.USER_NOT_SUPERADMIN);
//        if(userMapper.selectByPrimaryKey(userId).getType()!=0) throw new CommonException(CommonErrorCode.USER_IS_ADMIN);
//        userMapper.toAdmin(1,userId);
//    }
//
//    @Override
//    public void backToUser(Long userId, Long adminId) {
//        if(userMapper.selectByPrimaryKey(adminId).getType()!=2) throw new CommonException(CommonErrorCode.USER_NOT_SUPERADMIN);
//        if(userMapper.selectByPrimaryKey(userId).getType()!=1) throw new CommonException(CommonErrorCode.USER_NOT_ADMIN);
//        userMapper.toAdmin(0,userId);
//        userMapper.classifyUser(null,userId);
//    }

    @Override
    public User getUserById(Long userId, Long targetId) {
        if(targetId==null)return userMapper.getUserById(userId);
        if(!userId.equals(targetId)&&!userMapper.getUserById(userId).getType().equals(1))throw new CommonException(CommonErrorCode.USER_NOT_ADMIN);
        return userMapper.getUserById(targetId);
    }

    @Override
    public Integer updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        Long targetId;
        if(updateUserRequest.getId()==null)targetId=userId;
        else if(!userId.equals(updateUserRequest.getId())&&!userMapper.getUserById(userId).getType().equals(1))throw new CommonException(CommonErrorCode.USER_NOT_ADMIN);
        else targetId=updateUserRequest.getId();
        User targetUser=userMapper.getUserById(targetId);
        if(targetUser==null)throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        return userMapper.updateByPrimaryKeySelective(User.builder().id(targetId).accountNum(targetUser.getAccountNum()).portrait(updateUserRequest.getPortrait()).email(updateUserRequest. getEmail()).gender(updateUserRequest.getGender()).grade(updateUserRequest.getGrade()).major(updateUserRequest. getMajor()).name(updateUserRequest.getName()).
                nickname(updateUserRequest.getNickname()).password(passwordUtil.convert(updateUserRequest.getPassword())).school(updateUserRequest.getSchool()).telephone(updateUserRequest.getTelephone()).type(updateUserRequest.getType()).build());
     }
//
//
//
//
//
//    @Override
//    public void classifyUser(Long organizerId, Long userId, Long adminId) {
//        if(userMapper.selectByPrimaryKey(adminId).getType()!=2) throw new CommonException(CommonErrorCode.USER_NOT_SUPERADMIN);
//        if(userMapper.selectByPrimaryKey(userId).getType()!=1) throw new CommonException(CommonErrorCode.USER_NOT_ADMIN);
//        userMapper.classifyUser(organizerId,userId);
//    }
//
//
//
//
//
}