package com.phoenix.paper.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.dto.PaperAndNoteData;
import com.phoenix.paper.entity.*;
import com.phoenix.paper.mapper.*;
import com.phoenix.paper.util.ShuaiDatabaseUtils;
import com.phoenix.paper.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ScheduledTasks {

    @Autowired
    private ShuaiDatabaseUtils shuaiDatabaseUtils;

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private PaperSumPerDayMapper paperSumPerDayMapper;

    @Autowired
    private NoteSumPerDayMapper noteSumPerDayMapper;

    @Autowired
    private LikesMapper likesMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private UserMapper userMapper;


    @Transactional(rollbackFor = CommonException.class)
    @Scheduled(cron = "0 0 0/12 * * ? ")
    public void likes2database() {
        Map<String, String> likeInformation = new HashMap<>();
        likeInformation = shuaiDatabaseUtils.hmget("LIKE_INFORMATION");
        Map<String, String> likeCount = new HashMap<>();
        likeCount = shuaiDatabaseUtils.hmget("LIKE_COUNT");
        shuaiDatabaseUtils.del("LIKE_COUNT");
        shuaiDatabaseUtils.del("LIKE_INFORMATION");
        for (Map.Entry<String, String> entry : likeInformation.entrySet()) {
            String information = (String) entry.getKey();
            String[] splitInfo = information.split(" ");
            String time = (String) entry.getValue();
            if (splitInfo[2].equals("1")) {
                Likes like = Likes.builder().objectId(Long.valueOf(splitInfo[1].substring(1))).objectType((int) splitInfo[1].charAt(0) - 48).userId(Long.valueOf(splitInfo[0])).likeTime(time).build();
                likesMapper.insert(like);
            } else if (splitInfo[2].equals("0")) {

                QueryWrapper<Likes> likesQueryWrapper = new QueryWrapper<>();
                likesQueryWrapper.eq("object_id", Long.parseLong(splitInfo[1].substring(1))).eq("object_type", (int) splitInfo[1].charAt(0) - 48);
                likesMapper.update(Likes.builder().deleteTime(time).build(), likesQueryWrapper);
            }
        }
        for (Map.Entry<String, String> entry : likeCount.entrySet()) {
            String object = (String) entry.getKey();
            Long likeNumber = Long.valueOf(entry.getValue());
            if (object.charAt(0) == '1') {
                Paper paper = Paper.builder().id(Long.valueOf(object.substring(2))).build();
                paper.setLikeNumber(likeNumber.intValue());
                if (paperMapper.updateById(paper) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
            } else if (object.charAt(0) == '0') {
                Note note = Note.builder().id(Long.valueOf(object.substring(2))).build();
                note.setLikeNumber(likeNumber.intValue());
                if (noteMapper.updateById(note) == 0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
            }
        }
        try {
            Thread.sleep(1000 * 5);
            shuaiDatabaseUtils.del("LIKE_COUNT");
            shuaiDatabaseUtils.del("LIKE_INFORMATION");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional(rollbackFor = CommonException.class)
    @Scheduled(cron = "0 0 0/12 * * ? ")
    public void collections2database() {
        Map<String, String> collectInformation = new HashMap<>();
        collectInformation = shuaiDatabaseUtils.hmget("COLLECT_INFORMATION");
        Map<String, String> collectCount = new HashMap<>();
        collectCount = shuaiDatabaseUtils.hmget("COLLECT_COUNT");
        shuaiDatabaseUtils.del("COLLECT_COUNT");
        shuaiDatabaseUtils.del("COLLECT_INFORMATION");
        if (collectInformation != null) {
            for (Map.Entry<String, String> entry : collectInformation.entrySet()) {
                String information = (String) entry.getKey();
                String[] splitInfo = information.split(" ");
                String time = (String) entry.getValue();
                if (splitInfo[2].equals("1")) {
                    Collection collection = Collection.builder().objectId(Long.valueOf(splitInfo[1].substring(1))).objectType((int) splitInfo[1].charAt(0) - 48).userId(Long.valueOf(splitInfo[0])).collectTime(time).build();
                    collectionMapper.insert(collection);
                } else if (splitInfo[2].equals("0")) {
                    Collection collection = collectionMapper.getCollect(Long.parseLong(splitInfo[1].substring(1)), Integer.valueOf(splitInfo[1].charAt(0) - 48));
                    if (collection != null) collection.setDeleteTime(TimeUtil.getCurrentTimestamp());
                    collectionMapper.updateById(collection);
                }
            }
        }
        if (collectCount != null) {
            for (Map.Entry<String, String> entry : collectCount.entrySet()) {
                String object = (String) entry.getKey();
                long collectNumber = Long.parseLong(entry.getValue());
                if (object.charAt(0) == '0') {
                    Paper paper = Paper.builder().id(Long.valueOf(object.substring(2))).build();
                    paper.setCollectNumber(Optional.ofNullable(paper.getCollectNumber()).orElse(0) + (int) collectNumber);
                    if (paperMapper.updateById(paper) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);

                } else if (object.charAt(0) == '1') {
                    Note note = Note.builder().id(Long.valueOf(object.substring(2))).build();
                    note.setCollectNumber(Optional.ofNullable(note.getCollectNumber()).orElse(0) + (int) collectNumber);
                    if (noteMapper.updateById(note) == 0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
                }
            }
        }
        try {
            Thread.sleep(1000 * 5);
            shuaiDatabaseUtils.del("COLLECT_COUNT");
            shuaiDatabaseUtils.del("COLLECT_INFORMATION");
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
        List<Long> allUserId = userMapper.allUserIdList();
        for(Long userId:allUserId){
            List<PaperAndNoteData> paperDataList =  paperMapper.getPaperData(userId,1);
            for(PaperAndNoteData paperData:paperDataList){
                PaperSumPerDay paperSumPerDay = paperSumPerDayMapper.selectOne(new QueryWrapper<>(PaperSumPerDay.builder().userId(userId).direction(paperData.getDirection()).build()));
                if(paperSumPerDay!=null) paperSumPerDay.setNumber(paperSumPerDay.getNumber()+paperData.getNumber());
                else paperSumPerDayMapper.insert(new PaperSumPerDay(null,userId, TimeUtil.getCurrentTimestamp(),paperData.getDirection(),paperData.getNumber()));
            }
            List<PaperAndNoteData> noteDataList =  noteMapper.getNoteData(userId,1);
            for(PaperAndNoteData noteData:noteDataList){
                NoteSumPerDay noteSumPerDay = noteSumPerDayMapper.selectOne(new QueryWrapper<>(NoteSumPerDay.builder().userId(userId).direction(noteData.getDirection()).build()));
                if(noteSumPerDay!=null) noteSumPerDay.setNumber(noteSumPerDay.getNumber()+noteData.getNumber());
                else noteSumPerDayMapper.insert(new NoteSumPerDay(null,userId, TimeUtil.getCurrentTimestamp(),noteData.getDirection(),noteData.getNumber()));
            }

        }
    }
}
