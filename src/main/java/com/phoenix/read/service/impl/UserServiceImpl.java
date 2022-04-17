package com.phoenix.read.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.read.common.*;
import com.phoenix.read.config.YmlConfig;
import com.phoenix.read.controller.request.UpdateUserRequest;
import com.phoenix.read.controller.response.LoginResponse;
import com.phoenix.read.dto.BriefUser;
import com.phoenix.read.dto.SessionData;
import com.phoenix.read.dto.WxSession;
import com.phoenix.read.entity.User;
import com.phoenix.read.mapper.UserMapper;
import com.phoenix.read.service.UserService;
import com.phoenix.read.util.*;
import com.phoenix.read.config.YmlConfig;
import com.phoenix.read.mapper.UserMapper;
import com.phoenix.read.util.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Service;

import java.util.*;

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
        redisUtils.set(sessionId,new SessionData(user),1);
        return new LoginResponse(new SessionData(user),sessionId);
    }

    @Override
    public Page<BriefUser> getBriefUserList(int pageSize, int pageNum,Long userId) {
        if(userMapper.selectByPrimaryKey(userId).getType()!=2) throw new CommonException(CommonErrorCode.USER_NOT_SUPERADMIN);
        PageHelper.startPage(pageNum,pageSize,"id asc");
        return new Page<>(new PageInfo<>(userMapper.getBriefUser()));
    }

    @Override
    public void toAdmin(Long userId,Long adminId) {
        if(userMapper.selectByPrimaryKey(adminId).getType()!=2) throw new CommonException(CommonErrorCode.USER_NOT_SUPERADMIN);
        if(userMapper.selectByPrimaryKey(userId).getType()!=0) throw new CommonException(CommonErrorCode.USER_IS_ADMIN);
        userMapper.toAdmin(1,userId);
    }

    @Override
    public void backToUser(Long userId, Long adminId) {
        if(userMapper.selectByPrimaryKey(adminId).getType()!=2) throw new CommonException(CommonErrorCode.USER_NOT_SUPERADMIN);
        if(userMapper.selectByPrimaryKey(userId).getType()!=1) throw new CommonException(CommonErrorCode.USER_NOT_ADMIN);
        userMapper.toAdmin(0,userId);
        userMapper.classifyUser(null,userId);
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public void UpdateUser(Long userId, UpdateUserRequest updateUserRequest) {
        userMapper.updateUser(updateUserRequest.getNickname(),updateUserRequest.getDepartment(),updateUserRequest.getMajor(),updateUserRequest.getGrade(),updateUserRequest.getTelephone(),updateUserRequest.getQQ(),updateUserRequest.getWechatNum(),updateUserRequest.getPortrait(),userId);
    }

    @Override
    public void classifyUser(Long organizerId, Long userId, Long adminId) {
        if(userMapper.selectByPrimaryKey(adminId).getType()!=2) throw new CommonException(CommonErrorCode.USER_NOT_SUPERADMIN);
        if(userMapper.selectByPrimaryKey(userId).getType()!=1) throw new CommonException(CommonErrorCode.USER_NOT_ADMIN);
        userMapper.classifyUser(organizerId,userId);
    }





}