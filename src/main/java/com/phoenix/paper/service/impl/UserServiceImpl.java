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

        AssertUtil.isTrue(passwordUtil.convert(password).equals(user.getPassword()),CommonErrorCode.PASSWORD_NOT_RIGHT);

        sessionUtils.setSessionId(sessionId);
        redisUtils.set(sessionId,new SessionData(user),1440);
        return new LoginResponse(new SessionData(user),sessionId);
    }

    @Override
    public Page<BriefUser> getBriefUserList(int pageSize, int pageNum,Long userId) {
        if(userMapper.getUserById(userId).getType()!=1) throw new CommonException(CommonErrorCode.USER_NOT_SUPERADMIN);
        PageHelper.startPage(pageNum,pageSize,"create_time desc");
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
    public void updateUser(Long userId, UpdateUserRequest updateUserRequest) {

     }

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