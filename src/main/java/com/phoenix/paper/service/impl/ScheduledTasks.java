package com.phoenix.paper.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.dto.PaperAndNoteData;
import com.phoenix.paper.entity.*;
import com.phoenix.paper.mapper.*;
import com.phoenix.paper.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ScheduledTasks {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private LikesMapper likesMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PaperSumPerDayMapper paperSumPerDayMapper;

    @Autowired
    private NoteSumPerDayMapper noteSumPerDayMapper;

    @Transactional(rollbackFor = CommonException.class)
    @Scheduled(cron = "0 0 0/12 * * ? ")
    public void likes2database() {
        Map<Object, Object> likeInformation = new HashMap<>();
        likeInformation = redisUtils.hmget("LIKE_INFORMATION");
        Map<Object, Object> likeCount = new HashMap<>();
        likeCount = redisUtils.hmget("LIKE_COUNT");
        redisUtils.del("LIKE_COUNT");
        redisUtils.del("LIKE_INFORMATION");
        for (Map.Entry<Object, Object> entry : likeInformation.entrySet()) {
            String information = (String) entry.getKey();
            String[] splitInfo = information.split(" ");
            String time = (String) entry.getValue();
            if(splitInfo[2].equals("1")){
                Likes like= Likes.builder().objectId(Long.valueOf(splitInfo[1].substring(1))).objectType((int) splitInfo[1].charAt(0) -48).userId(Long.valueOf(splitInfo[0])).likeTime(time).build();
                likesMapper.insert(like);
            }
            else if(splitInfo[2].equals("0")){

                QueryWrapper<Likes> likesQueryWrapper = new QueryWrapper<>();
                likesQueryWrapper.eq("object_id",Long.parseLong(splitInfo[1].substring(1))).eq("object_type",(int) splitInfo[1].charAt(0) -48);
                likesMapper.update(Likes.builder().deleteTime(time).build(),likesQueryWrapper);
            }
        }
        for (Map.Entry<Object, Object> entry : likeCount.entrySet()){
            String object=(String) entry.getKey();
            Long likeNumber=(Long) entry.getValue();
            if(object.charAt(0)=='1') {
                Paper paper = Paper.builder().id(Long.valueOf(object.substring(2))).build();
                paper.setLikeNumber(likeNumber.intValue());
                if (paperMapper.updateById(paper) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
            }
            else if(object.charAt(0)=='0') {
                Note note = Note.builder().id(Long.valueOf(object.substring(2))).build();
                note.setLikeNumber(likeNumber.intValue());
                if (noteMapper.updateById(note) == 0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
            }
        }
        try {
            Thread.sleep(1000 * 5);
            redisUtils.del("LIKE_COUNT");
            redisUtils.del("LIKE_INFORMATION");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional(rollbackFor = CommonException.class)
    @Scheduled(cron = "0 0 0/12 * * ? ")
    public void collections2database() {
        Map<Object, Object> collectInformation = new HashMap<>();
        collectInformation = redisUtils.hmget("COLLECT_INFORMATION");
        Map<Object, Object> collectCount = new HashMap<>();
        collectCount = redisUtils.hmget("LIKE_COUNT");
        redisUtils.del("COLLECT_COUNT");
        redisUtils.del("COLLECT_INFORMATION");
        for (Map.Entry<Object, Object> entry : collectInformation.entrySet()) {
            String information = (String) entry.getKey();
            String[] splitInfo = information.split(" ");
            String time = (String) entry.getValue();
            if(splitInfo[2].equals("1")){
                Collection collection= Collection.builder().objectId(Long.valueOf(splitInfo[1].substring(1))).objectType((int) splitInfo[1].charAt(0) -48).userId(Long.valueOf(splitInfo[0])).collectTime(time).build();
                collectionMapper.insert(collection);
            }
            else if(splitInfo[2].equals("0")){
                QueryWrapper<Collection> collectionQueryWrapper = new QueryWrapper<>();
                collectionQueryWrapper.eq("object_id",Long.parseLong(splitInfo[1].substring(1))).eq("object_type", (int) splitInfo[1].charAt(0) -48);
                if(collectionMapper.update(Collection.builder().deleteTime(time).build(), collectionQueryWrapper)==0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
            }
        }
        for (Map.Entry<Object, Object> entry : collectCount.entrySet()){
            String object=(String) entry.getKey();
            Long collectNumber=(Long) entry.getValue();
            if(object.charAt(0)=='1') {
                Paper paper = Paper.builder().id(Long.valueOf(object.substring(2))).build();
                paper.setCollectNumber(collectNumber.intValue());
                if (paperMapper.updateById(paper) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);

            }
            else if(object.charAt(0)=='0') {
                Note note = Note.builder().id(Long.valueOf(object.substring(2))).build();
                note.setCollectNumber(collectNumber.intValue());
                if (noteMapper.updateById(note) == 0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
            }
        }
        try {
            Thread.sleep(1000*5);
            redisUtils.del("COLLECT_COUNT");
            redisUtils.del("COLLECT_INFORMATION");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Transactional(rollbackFor = CommonException.class)
    @Scheduled(cron = "0 0 0 0/7 * ? ")
    public void clearTheUserNum(){
        List<User> userList = userMapper.selectList(new QueryWrapper<>());
        for(User user:userList){
            user.setPaperWeekNum(0);
            user.setNoteWeekNum(0);
            if (userMapper.updateById(user) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
        }
    }

    @Scheduled(cron = "0 0 0 0/24 * ? ")
    public void updateUserData(){
        List<User> userList= userMapper.selectList(new QueryWrapper<>());
        for(User user:userList){
            List<PaperAndNoteData> paperDataList=paperMapper.getPaperData(user.getId(),7);
            for(PaperAndNoteData paperData:paperDataList) {
                PaperSumPerDay paperSumPerDay= paperSumPerDayMapper.selectOne(new QueryWrapper<>(PaperSumPerDay.builder().id(user.getId()).direction(paperData.getDirection()).build()));
                Integer change=paperSumPerDay.getNumber_week()-paperData.getNumber();
                paperSumPerDay.setNumber_week(paperSumPerDay.getNumber_week()-change);
                paperSumPerDay.setNumber_month(paperSumPerDay.getNumber_month()-change);
                paperSumPerDay.setNumber_year(paperSumPerDay.getNumber_year()-change);
            }
            List<PaperAndNoteData> noteDataList=noteMapper.getNoteData(user.getId(),7);
            for(PaperAndNoteData noteData:noteDataList) {
                NoteSumPerDay noteSumPerDay= noteSumPerDayMapper.selectOne(new QueryWrapper<>(NoteSumPerDay.builder().id(user.getId()).direction(noteData.getDirection()).build()));
                Integer change=noteSumPerDay.getNumber_week()-noteData.getNumber();
                noteSumPerDay.setNumber_week(noteSumPerDay.getNumber_week()-change);
                noteSumPerDay.setNumber_month(noteSumPerDay.getNumber_month()-change);
                noteSumPerDay.setNumber_year(noteSumPerDay.getNumber_year()-change);
            }
        }
    }

}
