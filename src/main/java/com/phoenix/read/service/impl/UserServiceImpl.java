package com.phoenix.read.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.read.common.*;
import com.phoenix.read.config.YmlConfig;
import com.phoenix.read.controller.request.UpdateUserRequest;
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


    @Override
    public SessionData login(String number,String password) {


        String sessionId = sessionUtils.generateSessionId();

        User user = User.builder()
                .build();
        user = userMapper.selectOne(user);

        if(user != null){
            sessionUtils.setSessionId(sessionId);
            redisUtils.set(user.getId().toString(),sessionId,1440);
            return new SessionData(user);
        }


        user = User.builder()
                .createTime(TimeUtil.getCurrentTimestamp())
                .sessionId(sessionId)
                .type(0)
                .isMute(0)
                .nickname("花狮用户")
                .build();


        return new SessionData(user);
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


    private WxSession getWxSessionByCode(String code){
        Map<String, String> requestUrlParam = new HashMap<>();
        //小程序appId
        requestUrlParam.put("appid", ymlConfig.getAppId());
//        requestUrlParam.put("appid", "wx22fa1182d4e66c4a");
        //小程序secret
        requestUrlParam.put("secret", ymlConfig.getAppSecret());
//        requestUrlParam.put("secret", "200e82982f7ec2a2812fc3ae9f2d5f15");
        //小程序端返回的code
        requestUrlParam.put("js_code", code);
        //默认参数：授权类型
        requestUrlParam.put("grant_type", "authorization_code");
        //发送post请求读取调用微信接口获取openid用户唯一标识
        String result = HttpUtil.get(CommonConstants.WX_SESSION_REQUEST_URL, requestUrlParam);
//        String result = HttpUtil.get("https://api.weixin.qq.com/sns/jscode2session", requestUrlParam);

        return JsonUtil.toObject(result, WxSession.class);
    }

    private void checkWxSession(WxSession wxSession){
        if(wxSession.getErrcode() != null) {
            AssertUtil.isFalse(-1 == wxSession.getErrcode(), CommonErrorCode.WX_LOGIN_BUSY, wxSession.getErrmsg());
            AssertUtil.isFalse(40029 == wxSession.getErrcode(), CommonErrorCode.WX_LOGIN_INVALID_CODE, wxSession.getErrmsg());
            AssertUtil.isFalse(45011 == wxSession.getErrcode(), CommonErrorCode.WX_LOGIN_FREQUENCY_REFUSED, wxSession.getErrmsg());
            AssertUtil.isTrue(wxSession.getErrcode() == 0, CommonErrorCode.WX_LOGIN_UNKNOWN_ERROR,wxSession.getErrmsg());
        }
    }



}