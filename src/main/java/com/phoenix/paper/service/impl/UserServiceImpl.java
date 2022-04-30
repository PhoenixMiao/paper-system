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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import java.sql.Time;
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

    //读取配置文件邮箱账号参数
    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    JavaMailSender jms;

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
        if(userMapper.selectByPrimaryKey(userId).getType()!=1) throw new CommonException(CommonErrorCode.USER_NOT_SUPERADMIN);
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
    public SessionData getUserById(Long userId) throws CommonException{
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null|| user.getDeleteTime()!=null) throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        return new SessionData(user);
    }

    @Override
    public Integer updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        Long targetId;
        if(updateUserRequest.getId()==null) targetId=userId;
        else if(!userId.equals(updateUserRequest.getId())&&!userMapper.selectByPrimaryKey(userId).getType().equals(1))throw new CommonException(CommonErrorCode.USER_NOT_ADMIN);
        else targetId=updateUserRequest.getId();
        User targetUser=userMapper.selectByPrimaryKey(targetId);
        if(targetUser==null)throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        return userMapper.updateByPrimaryKeySelective(User.builder().id(targetId).accountNum(targetUser.getAccountNum()).portrait(updateUserRequest.getPortrait()).email(updateUserRequest. getEmail()).gender(updateUserRequest.getGender()).grade(updateUserRequest.getGrade()).major(updateUserRequest. getMajor()).name(updateUserRequest.getName()).
                nickname(updateUserRequest.getNickname()).password(passwordUtil.convert(updateUserRequest.getPassword())).school(updateUserRequest.getSchool()).telephone(updateUserRequest.getTelephone()).type(updateUserRequest.getType()).build());
     }


    @Override
    public LoginResponse signUp(String email,String password,String verificationCode)throws CommonException{
        if(redisUtils.hasKey(email)) throw new CommonException((CommonErrorCode.HAS_NOT_SENT_EMAIL));
        if(redisUtils.isExpire(email)) throw new CommonException(CommonErrorCode.VERIFICATION_CODE_HAS_EXPIRED);
        redisUtils.del(email);
        String sessionId = sessionUtils.generateSessionId();
        User user =  User.builder()
                .createTime(TimeUtil.getCurrentTimestamp())
                .email(email)
                .gender(0)
                .password(passwordUtil.convert(password))
                .type(0)
                .nickname("论文平台用户")
                .build();
        userMapper.insert(user);
        user.setAccountNum("ps" + String.format("%08d", user.getId()));
        userMapper.updateByPrimaryKeySelective(User.builder().id(user.getId()).accountNum(user.getAccountNum()).build());
        return new LoginResponse(new SessionData(user),sessionId);
    }


    @Override
    public String sendEmail(String email){
        if(redisUtils.hasKey(email)) redisUtils.del(email);
        String verificationCode = RandomVerifyCodeUtil.getRandomVerifyCode();
        redisUtils.set(email,verificationCode,5);
        try {
            //建立邮件消息
            SimpleMailMessage mainMessage = new SimpleMailMessage();

            //发送者
            mainMessage.setFrom(sender);

            //接收者
            mainMessage.setTo(email);

            //发送的标题
            mainMessage.setSubject("邮箱验证");

            //发送的内容
            String msg = "论文平台：您好！" + email + ",您正在使用邮箱验证，验证码：" + verificationCode + "。";
            mainMessage.setText(msg);

            //发送邮件
            jms.send(mainMessage);

        } catch (Exception e) {
            throw new CommonException(CommonErrorCode.SEND_EMAIL_FAILED);
        }
        return verificationCode;
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