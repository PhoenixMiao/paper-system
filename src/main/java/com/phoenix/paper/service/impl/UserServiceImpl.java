package com.phoenix.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.paper.common.*;
import com.phoenix.paper.config.YmlConfig;
import com.phoenix.paper.controller.request.UpdateUserRequest;
import com.phoenix.paper.controller.response.LoginResponse;
import com.phoenix.paper.dto.BriefUser;
import com.phoenix.paper.dto.SessionData;
import com.phoenix.paper.entity.*;
import com.phoenix.paper.mapper.*;
import com.phoenix.paper.service.UserService;
import com.phoenix.paper.util.*;
import com.phoenix.paper.util.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Wrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private LikesMapper likesMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private YmlConfig ymlConfig;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private PaperQuotationMapper paperQuotationMapper;

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
        if(userMapper.selectById(userId).getType()!=1) throw new CommonException(CommonErrorCode.USER_NOT_SUPERADMIN);
        PageHelper.startPage(pageNum,pageSize,"create_time desc");
        //List<BriefUser> briefUsers = briefUserList.stream().parallel().filter(user -> user.getDeleteTime() == null).collect(Collectors.toList());
        return new Page<>(new PageInfo<>(userMapper.getBriefUserList()));
    }

    @Override
    public void toAdmin(Long userId) {
        User user = userMapper.selectById(userId);
        if(user == null|| user.getDeleteTime()!=null) throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        if(user.getType()==1) throw new CommonException(CommonErrorCode.USER_IS_ADMIN);
        synchronized (this) {
            userMapper.toAdmin(1, userId);
        }
    }

    @Override
    public SessionData getUserById(Long userId) throws CommonException{
        User user = userMapper.selectById(userId);
        if(user == null|| user.getDeleteTime()!=null) throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        return new SessionData(user);
    }

    @Override
    public void updateEmail(String email,Long userId) throws CommonException{
        User user=userMapper.selectById(userId);
        user.setEmail(email);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email",email);
        if(userMapper.selectList(userQueryWrapper).size()!=0) throw new CommonException(CommonErrorCode.EMAIL_HAS_BEEN_SIGNED_UP);
        user.setEmail(email);
        if(userMapper.updateById(user)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
    }

    @Override
    public void updateUser(Long userId, UpdateUserRequest updateUserRequest) throws CommonException{
        User user=userMapper.selectById(userId);

        if(updateUserRequest.getPortrait()!=null) user.setPortrait(updateUserRequest.getPortrait());
        if(updateUserRequest.getGender()!=null) user.setGender(updateUserRequest.getGender()) ;
        if(updateUserRequest.getGrade()!=null) user.setGrade(updateUserRequest.getGrade());
        if(updateUserRequest.getSchool()!=null) user.setSchool(updateUserRequest.getSchool());
        if(updateUserRequest.getNickname()!=null) user.setNickname(updateUserRequest.getNickname());
        if(updateUserRequest.getMajor()!=null) user.setMajor(updateUserRequest.getMajor());
        if(updateUserRequest.getTelephone()!=null) user.setTelephone(updateUserRequest.getTelephone());
        if(updateUserRequest.getName()!=null) user.setName(updateUserRequest.getName());

        if(userMapper.updateById(user)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
    }

    @Override
    public LoginResponse signUp(String email,String password)throws CommonException{
        if(userMapper.selectByMap((Map<String, Object>) new HashMap<String,Object>().put("email",email)).size()!=0){
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
                .version(1)
                .build();
        userMapper.insert(user);
        user.setAccountNum("ps" + String.format("%08d", user.getId()));
        if(userMapper.updateById(user)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
        redisUtils.set(sessionId,new SessionData(user),1440);
        return new LoginResponse(new SessionData(user),sessionId);
    }

    @Override
    public String findNumber(String email){
        List<User> userList = userMapper.selectByMap((Map<String, Object>) new HashMap<String,Object>().put("email",email));
        if(userList.size()==0){
            throw new CommonException(CommonErrorCode.EMAIL_NOT_SIGNED_UP);
        }
        User user = userList.get(0);
        return user.getAccountNum();
    }

    @Override
    public void updatePassword(String accountNum,String password) throws CommonException{
        User user = userMapper.selectById(Long.parseLong(accountNum.substring(2)));
        if(user==null || user.getDeleteTime()!=null) throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        if(!passwordUtil.EvalPWD(password)) throw new CommonException(CommonErrorCode.PASSWORD_NOT_QUANTIFIED);
        user.setPassword(passwordUtil.convert(password));
        if(userMapper.updateById(user)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
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
        Map<String,Object> map = new HashMap<>();
        if(flag==0){
            map.put("email",emailOrNumber);
            if(userMapper.selectByMap(map).size()!=0){
                throw new CommonException(CommonErrorCode.EMAIL_HAS_BEEN_SIGNED_UP);
            }
        }else if(flag==1){
            map.put("email",emailOrNumber);
            if(userMapper.selectByMap(map).size()==0){
                throw new CommonException(CommonErrorCode.EMAIL_NOT_SIGNED_UP);
            }
        }else{
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("account_num",emailOrNumber);
            User user = userMapper.selectOne(userQueryWrapper);
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

    @Override
    public void deleteUser(Long userId){
        User user=userMapper.selectById(userId);
        if(user==null || user.getDeleteTime()!=null)throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        String deleteTime = TimeUtil.getCurrentTimestamp();
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Paper> paperQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Note> noteQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Likes> likesQueryWrapper = new QueryWrapper<>();

        synchronized (this) {
            user.setDeleteTime(TimeUtil.getCurrentTimestamp());
            userMapper.updateById(user);
            paperQueryWrapper.eq("uploader_id",userId);
            List<Paper> papers = paperMapper.selectList(paperQueryWrapper);
            for (Paper paper : papers) {
                noteQueryWrapper.eq("paper_id",paper.getId());
                List<Note> notes = noteMapper.selectList(noteQueryWrapper);
                for (Note note : notes) {
                    likesMapper.cancelLike(deleteTime, note.getId(), 1);
                    collectionMapper.cancelCollect(deleteTime, note.getId(), 1);
                    commentQueryWrapper.eq("object_id",note.getId());
                    commentQueryWrapper.eq("object_type",0);
                    List<Comment> comments = commentMapper.selectList(commentQueryWrapper);
                    for (Comment comment : comments) {
                        commentMapper.cancelComment(deleteTime, comment.getObjectId(), 1);
                    }
                    commentMapper.cancelComment(deleteTime, note.getId(), 0);
                }
                noteMapper.deleteNoteByPaperId(deleteTime, paper.getId());
                likesMapper.cancelLike(deleteTime, paper.getId(), 0);
                collectionMapper.cancelCollect(deleteTime, paper.getId(), 0);
                paperQuotationMapper.deletePaper(deleteTime, paper.getId(), paper.getId());
            }
            paperMapper.deletePaperByUploaderId(deleteTime, userId);
            noteQueryWrapper = new QueryWrapper<>();
            noteQueryWrapper.eq("author_id",userId);
            List<Note> notes = noteMapper.selectList(noteQueryWrapper);
            for (Note note : notes) {
                likesMapper.cancelLike(deleteTime, note.getId(), 1);
                collectionMapper.cancelCollect(deleteTime, note.getId(), 1);
                commentQueryWrapper = new QueryWrapper<>();
                commentQueryWrapper.eq("object_id",note.getId());
                commentQueryWrapper.eq("object_type",0);
                List<Comment> comments = commentMapper.selectList(commentQueryWrapper);
                for (Comment comment : comments) {
                    commentMapper.cancelComment(deleteTime, comment.getObjectId(), 1);
                }
                commentMapper.cancelComment(deleteTime, note.getId(), 0);
            }
            noteMapper.deleteNoteByAuthorId(deleteTime, userId);
            likesMapper.deleteLike(deleteTime, userId);
            collectionMapper.deleteCollect(deleteTime, userId);
            commentMapper.deleteComment(deleteTime, userId);
        }
    }

    @Override
    public void authorizeUser(Long userId,Integer type){
        User user=userMapper.selectById(userId);
        if (user==null || user.getDeleteTime()!=null)throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        user.setType(type);
        userMapper.updateById(user);
    }

}
