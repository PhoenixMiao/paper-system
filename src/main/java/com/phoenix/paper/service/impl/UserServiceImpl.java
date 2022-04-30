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

import javax.jws.soap.SOAPBinding;
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

    @Autowired
    private MessageUtil messageUtil;

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
    public LoginResponse signUp(String email,String password)throws CommonException{
        if(userMapper.select(User.builder().email(email).build()).size()!=0){
            throw new CommonException(CommonErrorCode.EMAIL_HAS_BEEN_SIGNED_UP);
        }
        if(!passwordUtil.EvalPWD(password)) throw new CommonException(CommonErrorCode.PASSWORD_NOT_QUANTIFIED);
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
    public String findNumber(String email){
        if(userMapper.select(User.builder().email(email).build()).size()==0){
            throw new CommonException(CommonErrorCode.EMAIL_NOT_SIGNED_UP);
        }
        User user = userMapper.selectOne(User.builder().email(email).build());
        return user.getAccountNum();
    }

    @Override
    public void updatePassword(String accountNum,String password){
        if(userMapper.selectOne(User.builder().accountNum(accountNum).build())==null) throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        if(!passwordUtil.EvalPWD(password)) throw new CommonException(CommonErrorCode.PASSWORD_NOT_QUANTIFIED);
        userMapper.updateByPrimaryKeySelective(User.builder().accountNum(accountNum).password(passwordUtil.convert(password)).build());
    }

    @Override
    public void checkCode(String email,String code) throws CommonException{
        if (!redisUtils.hasKey(email)) throw new CommonException((CommonErrorCode.HAS_NOT_SENT_EMAIL));
        if (redisUtils.isExpire(email)) throw new CommonException(CommonErrorCode.VERIFICATION_CODE_HAS_EXPIRED);
        if (redisUtils.get(email) != code)
            throw new CommonException(CommonErrorCode.VERIFICATION_CODE_WRONG);
        redisUtils.del(email);
    }

    @Override
    public String sendEmail(String emailOrNumber,int flag){
        if(flag==0){
            if(userMapper.select(User.builder().email(emailOrNumber).build()).size()!=0){
                throw new CommonException(CommonErrorCode.EMAIL_HAS_BEEN_SIGNED_UP);
            }
        }else if(flag==1){
            if(userMapper.select(User.builder().email(emailOrNumber).build()).size()==0){
                throw new CommonException(CommonErrorCode.EMAIL_NOT_SIGNED_UP);
            }
        }else{
            User user = userMapper.selectOne(User.builder().accountNum(emailOrNumber).build());
            if(user==null){
                throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
            }
            emailOrNumber = user.getEmail();
        }
        //if(redisUtils.hasKey(email)) redisUtils.del(email);
        String verificationCode = RandomVerifyCodeUtil.getRandomVerifyCode();
        redisUtils.set(emailOrNumber,verificationCode,5);
        try {
            messageUtil.sendMail(sender,emailOrNumber,verificationCode,jms,flag);
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