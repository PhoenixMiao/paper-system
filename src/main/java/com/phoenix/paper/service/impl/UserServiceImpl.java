package com.phoenix.paper.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.paper.common.CommonConstants;
import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.common.Page;
import com.phoenix.paper.config.YmlConfig;
import com.phoenix.paper.controller.request.UpdateUserRequest;
import com.phoenix.paper.controller.response.LoginResponse;
import com.phoenix.paper.dto.BriefPaper;
import com.phoenix.paper.dto.BriefUser;
import com.phoenix.paper.dto.PaperAndNoteData;
import com.phoenix.paper.dto.SessionData;
import com.phoenix.paper.entity.Collection;
import com.phoenix.paper.entity.*;
import com.phoenix.paper.mapper.*;
import com.phoenix.paper.service.UserService;
import com.phoenix.paper.util.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.phoenix.paper.common.CommonConstants.USER_FILE_PATH;

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

    @Autowired
    private PaperDirectionMapper paperDirectionMapper;

    @Override
    public LoginResponse login(String number, String password) throws CommonException {

        String sessionId = sessionUtils.generateSessionId();

        User user = userMapper.getUserByNum(number);

        AssertUtil.notNull(user, CommonErrorCode.USER_NOT_EXIST);

        AssertUtil.isNull(user.getDeleteTime(), CommonErrorCode.USER_NOT_EXIST);

        AssertUtil.isTrue(passwordUtil.convert(password).equals(user.getPassword()), CommonErrorCode.PASSWORD_NOT_RIGHT);

        sessionUtils.setSessionId(sessionId);

        redisUtils.set(sessionId, new SessionData(user), 86400);

        return new LoginResponse(new SessionData(user), sessionId);
    }

    @Override
    public Page<BriefUser> getBriefUserList(int pageSize, int pageNum, Long userId) {
        if (userMapper.selectById(userId).getType() != 1)
            throw new CommonException(CommonErrorCode.USER_NOT_SUPERADMIN);
        PageHelper.startPage(pageNum, pageSize, "can_modify,name asc");
        //List<BriefUser> briefUsers = briefUserList.stream().parallel().filter(user -> user.getDeleteTime() == null).collect(Collectors.toList());
        return new Page<>(new PageInfo<>(userMapper.getBriefUserList()));
    }

    @Override
    public void toAdmin(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleteTime() != null) throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        if (user.getType() == 1) throw new CommonException(CommonErrorCode.USER_IS_ADMIN);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id",userId);
        if(userMapper.update(User.builder().type(1).build(), userQueryWrapper)==0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
    }

    @Override
    public SessionData getUserById(Long userId) throws CommonException {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleteTime() != null) throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        return new SessionData(user);
    }

    @Override
    public void updateEmail(String email, Long userId) throws CommonException {
        User user = userMapper.selectById(userId);
        user.setEmail(email);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email", email);
        if (userMapper.selectList(userQueryWrapper).size() != 0)
            throw new CommonException(CommonErrorCode.EMAIL_HAS_BEEN_SIGNED_UP);
        user.setEmail(email);
        if (userMapper.updateById(user) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
    }

    @Override
    public void updateUser(Long userId, UpdateUserRequest updateUserRequest) throws CommonException {
        User user = userMapper.selectById(userId);

        if (updateUserRequest.getGender() != null) user.setGender(updateUserRequest.getGender());
        if (updateUserRequest.getGrade() != null) user.setGrade(updateUserRequest.getGrade());
        if (updateUserRequest.getSchool() != null) user.setSchool(updateUserRequest.getSchool());
        if (updateUserRequest.getNickname() != null) user.setNickname(updateUserRequest.getNickname());
        if (updateUserRequest.getMajor() != null) user.setMajor(updateUserRequest.getMajor());
        if (updateUserRequest.getTelephone() != null) user.setTelephone(updateUserRequest.getTelephone());
        if (updateUserRequest.getName() != null) user.setName(updateUserRequest.getName());

        if (userMapper.updateById(user) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
    }

    @Override
    public LoginResponse signUp(String email, String password) throws CommonException {
        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();
        userQueryWrapper.eq("email",email).isNull("delete_time");
        if((userMapper.selectList(userQueryWrapper)).size()!=0){
            throw new CommonException(CommonErrorCode.EMAIL_HAS_BEEN_SIGNED_UP);
        }
        if (!passwordUtil.EvalPWD(password)) throw new CommonException(CommonErrorCode.PASSWORD_NOT_QUANTIFIED);
        String sessionId = sessionUtils.generateSessionId();
        User user = User.builder()
                .createTime(TimeUtil.getCurrentTimestamp())
                .email(email)
                .gender(0)
                .password(passwordUtil.convert(password))
                .type(0)
                .nickname("论文平台用户")
                .version(1)
                .canComment(1)
                .canModify(0)
                .build();
        userMapper.insert(user);
        user.setAccountNum("ps" + String.format("%08d", user.getId()));
        if (userMapper.updateById(user) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
        redisUtils.set(sessionId, new SessionData(user), 86400);
        return new LoginResponse(new SessionData(user), sessionId);
    }

    @Override
    public String findNumber(String email) {
        List<User> userList = userMapper.selectByMap((Map<String, Object>) new HashMap<String, Object>().put("email", email));
        if (userList.size() == 0) {
            throw new CommonException(CommonErrorCode.EMAIL_NOT_SIGNED_UP);
        }
        User user = userList.get(0);
        return user.getAccountNum();
    }

    @Override
    public void updatePassword(String accountNum, String password) throws CommonException {
        User user = userMapper.selectById(Long.parseLong(accountNum.substring(2)));
        if (user == null || user.getDeleteTime() != null) throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        if (!passwordUtil.EvalPWD(password)) throw new CommonException(CommonErrorCode.PASSWORD_NOT_QUANTIFIED);
        user.setPassword(passwordUtil.convert(password));
        if (userMapper.updateById(user) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
    }

    @Override
    public void checkCode(String email, String code) throws CommonException {
        //if (!redisUtils.hasKey(email)) throw new CommonException((CommonErrorCode.HAS_NOT_SENT_EMAIL));
        if (redisUtils.isExpire(email)) throw new CommonException(CommonErrorCode.VERIFICATION_CODE_HAS_EXPIRED);
        if (!redisUtils.get(email).equals(code)) throw new CommonException(CommonErrorCode.VERIFICATION_CODE_WRONG);
        else redisUtils.del(email);
    }

    @Override
    public String sendEmail(String emailOrNumber, int flag) {
        Map<String, Object> map = new HashMap<>();
        if (flag == 0) {
            map.put("email", emailOrNumber);
            if (userMapper.selectByMap(map).size() != 0) {
                throw new CommonException(CommonErrorCode.EMAIL_HAS_BEEN_SIGNED_UP);
            }
        } else if (flag == 1) {
            map.put("email", emailOrNumber);
            if (userMapper.selectByMap(map).size() == 0) {
                throw new CommonException(CommonErrorCode.EMAIL_NOT_SIGNED_UP);
            }
        } else {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("account_num", emailOrNumber);
            User user = userMapper.selectOne(userQueryWrapper);
            if (user == null) {
                throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
            }
            emailOrNumber = user.getEmail();
        }
//        if (redisUtils.hasKey(emailOrNumber) && redisUtils.getExpire(emailOrNumber) > 240) {
//            throw new CommonException(CommonErrorCode.DO_NOT_SEND_VERIFICATION_CODE_AGAIN);
//        } else
        if (redisUtils.isExpire(emailOrNumber)) {
            redisUtils.del(emailOrNumber);
        }
        String verificationCode = RandomVerifyCodeUtil.getRandomVerifyCode();
        redisUtils.set(emailOrNumber, verificationCode, 3000);
        try {
            messageUtil.sendMail(sender, emailOrNumber, verificationCode, jms, flag);
        } catch (Exception e) {
            throw new CommonException(CommonErrorCode.SEND_EMAIL_FAILED);
        }
        return verificationCode;
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) throws CommonException {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleteTime() != null) throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        String deleteTime = TimeUtil.getCurrentTimestamp();
        user.setDeleteTime(TimeUtil.getCurrentTimestamp());
        if (userMapper.updateById(user) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
        QueryWrapper<Paper> paperQueryWrapper = new QueryWrapper<>();
        paperQueryWrapper.eq("uploader_id", userId);
        List<Paper> papers = paperMapper.selectList(paperQueryWrapper);
        if(papers.size()!=0){
        for (Paper paper : papers) {
            QueryWrapper<Note> noteQueryWrapper = new QueryWrapper<>();
            noteQueryWrapper.eq("paper_id", paper.getId());
            List<Note> notes = noteMapper.selectList(noteQueryWrapper);
            for (Note note : notes) {
                note.setDeleteTime(deleteTime);
                noteMapper.updateById(note);
                QueryWrapper<Likes> likesQueryWrapper = new QueryWrapper<>();
                likesQueryWrapper.eq("object_id", note.getId()).eq("object_type", 1);
                likesMapper.update(Likes.builder().deleteTime(deleteTime).build(), likesQueryWrapper);
                QueryWrapper<Collection> collectionQueryWrapper = new QueryWrapper<>();
                collectionQueryWrapper.eq("object_id", note.getId()).eq("object_type", 1);
                collectionMapper.update(Collection.builder().deleteTime(deleteTime).build(), collectionQueryWrapper);
                QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
                commentQueryWrapper.eq("object_id", note.getId()).eq("object_type", 0);
                List<Comment> comments = commentMapper.selectList(commentQueryWrapper);
                for (Comment comment : comments) {
                    QueryWrapper<Comment> commentQueryWrapper1 = new QueryWrapper<>();
                    commentQueryWrapper1.eq("object_id", comment.getId()).eq("object_type", 1);
                    commentMapper.update(Comment.builder().deleteTime(deleteTime).build(), commentQueryWrapper1);
                }
                commentMapper.update(Comment.builder().deleteTime(deleteTime).build(), commentQueryWrapper);

            }
            QueryWrapper<Likes> likesQueryWrapper = new QueryWrapper<>();
            likesQueryWrapper.eq("object_id", paper.getId()).eq("object_type", 0);
            likesMapper.update(Likes.builder().deleteTime(deleteTime).build(), likesQueryWrapper);
            QueryWrapper<Collection> collectionQueryWrapper = new QueryWrapper<>();
            collectionQueryWrapper.eq("object_id", paper.getId()).eq("object_type", 0);
            collectionMapper.update(Collection.builder().deleteTime(deleteTime).build(), collectionQueryWrapper);
            QueryWrapper<PaperQuotation> paperQuotationQueryWrapper = new QueryWrapper<>();
            paperQuotationQueryWrapper.eq("quoter_id", paper.getId()).or().eq("quoted_id", paper.getId());
            paperQuotationMapper.update(PaperQuotation.builder().deleteTime(deleteTime).build(), paperQuotationQueryWrapper);
            QueryWrapper<PaperDirection> paperDirectionQueryWrapper = new QueryWrapper<>();
            paperDirectionMapper.update(PaperDirection.builder().deleteTime(deleteTime).build(), paperDirectionQueryWrapper);
        }
        }
        QueryWrapper<Note> noteQueryWrapper = new QueryWrapper<>();
        noteQueryWrapper.eq("author_id", userId);
        noteMapper.update(Note.builder().deleteTime(deleteTime).build(), noteQueryWrapper);
        QueryWrapper<Likes> likesQueryWrapper = new QueryWrapper<>();
        likesQueryWrapper.eq("user_id", userId);
        likesMapper.update(Likes.builder().deleteTime(deleteTime).build(), likesQueryWrapper);
        QueryWrapper<Collection> collectionQueryWrapper = new QueryWrapper<>();
        collectionQueryWrapper.eq("user_id", userId);
        collectionMapper.update(Collection.builder().deleteTime(deleteTime).build(), collectionQueryWrapper);
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("user_id", userId);
        commentMapper.update(Comment.builder().deleteTime(deleteTime).build(), commentQueryWrapper);
    }

    @Override
    public void upgradeUser(Long userId, Integer canModify) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleteTime() != null) throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        user.setCanModify(canModify);
        userMapper.updateById(user);
    }

    @Override
    public void muteUser(Long userId) throws CommonException {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleteTime() != null) throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        user.setCanComment(0);
        userMapper.updateById(user);
        new MuteThead(userId).updateStatus();
    }

    @Override
    public Page<BriefPaper> getUserPaperList(Integer pageNum, Integer pageSize, Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleteTime() != null) throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        PageHelper.startPage(pageNum, pageSize, "upload_time desc");
        return new Page<>(new PageInfo<>(paperMapper.getUserPaperList(userId)));
    }

    @Override
    public List<PaperAndNoteData> getUserPaperData(Integer period, Long userId){
        if (period != 7 && period != 30 && period != 365)
            throw new CommonException(CommonErrorCode.PERIOD_NOT_SUPPORTED);
        return paperMapper.getPaperData(userId, period);
    }

    @Override
    public List<PaperAndNoteData> getUserNoteData(Integer period, Long userId) {
        if (period != 7 && period != 30 && period != 365)
            throw new CommonException(CommonErrorCode.PERIOD_NOT_SUPPORTED);
        return noteMapper.getNoteData(userId, period);
    }

    @Override
    public String uploadPortrait(MultipartFile file, Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleteTime() != null) throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        String originalFilename = file.getOriginalFilename();
        String flag = IdUtil.fastSimpleUUID();
        String rootFilePath = USER_FILE_PATH + flag + "-" + originalFilename;
        try {
            FileUtil.writeBytes(file.getBytes(), rootFilePath);
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.READ_FILE_ERROR);
        }
        String link = CommonConstants.DOWNLOAD_NOTE_PATH + flag;
        user.setPortrait(link);
        if (userMapper.updateById(user) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
        return link;
    }

}


@NoArgsConstructor
@AllArgsConstructor
@Builder
class MuteThead {
    private Long id;

    public void updateStatus() {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date time = calendar.getTime();
        Timer timer = new Timer();
        timer.schedule(new Mute(id), time);
    }
}

class Mute extends TimerTask {
    private UserMapper userMapper;

    private Long id;

    public Mute(Long id) {
        super();
        userMapper = SpringContextUtil.getBean("UserMapper");
        this.id = id;
    }

    public Mute() {
        super();
    }


    @Override
    public void run() {
        try {
            if (userMapper == null) {  //这个判断是用老方法@Autowired注入的时候 报空指针 测试的时候在这儿判断了一下 是因为service空 没有成功注入 所有service/dao注入需要SpringContextUtil.getBean才可以
                System.out.println("---> null");
            }
            this.userMapper.updateById(User.builder().id(this.id).canComment(1).build());//这里 调用service的业务逻辑

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
