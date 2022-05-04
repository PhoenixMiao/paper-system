package com.phoenix.paper.service.impl;



import com.phoenix.paper.entity.Collection;
import com.phoenix.paper.entity.Likes;
import com.phoenix.paper.entity.Paper;
import com.phoenix.paper.mapper.CollectionMapper;
import com.phoenix.paper.mapper.LikesMapper;
import com.phoenix.paper.mapper.NoteMapper;
import com.phoenix.paper.mapper.PaperMapper;
import com.phoenix.paper.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    @Scheduled(cron = "0 0/10 * * * ? ")
    //@Scheduled(cron = "0 0 0/2 * * ? ")
    public void likes2database() {
        Map<Object,Object> likeInformation = new HashMap<>();
        likeInformation=redisUtils.hmget("LIKE_INFORMATION");
        Map<Object,Object> likeCount = new HashMap<>();
        likeCount=redisUtils.hmget("LIKE_COUNT");
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
                likesMapper.cancelLike(time,Long.parseLong(splitInfo[1].substring(1)), (int) splitInfo[1].charAt(0) -48);
            }
        }
        for (Map.Entry<Object, Object> entry : likeCount.entrySet()){
            String object=(String) entry.getKey();
            Long likeNumber=(Long) entry.getValue();
            if(object.charAt(0)=='1'){
                paperMapper.setPaperLikes(Long.valueOf(object.substring(2)),likeNumber);
            }
            else if(object.charAt(0)=='0'){
                noteMapper.setNoteLikes(Long.valueOf(object.substring(2)),likeNumber);
            }
        }
        try {
            Thread.sleep(1000*5);
            redisUtils.del("LIKE_COUNT");
            redisUtils.del("LIKE_INFORMATION");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0/10 * * * ? ")
    //@Scheduled(cron = "0 0 0/2 * * ? ")
    public void collections2database() {
        Map<Object,Object> collectInformation = new HashMap<>();
        collectInformation=redisUtils.hmget("COLLECT_INFORMATION");
        Map<Object,Object> collectCount = new HashMap<>();
        collectCount=redisUtils.hmget("LIKE_COUNT");
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
                collectionMapper.cancelCollect(time,Long.parseLong(splitInfo[1].substring(1)), (int) splitInfo[1].charAt(0) -48);
            }
        }
        for (Map.Entry<Object, Object> entry : collectCount.entrySet()){
            String object=(String) entry.getKey();
            Long collectNumber=(Long) entry.getValue();
            if(object.charAt(0)=='1'){
                paperMapper.setPaperCollects(Long.valueOf(object.substring(2)),collectNumber);
            }
            else if(object.charAt(0)=='0'){
                noteMapper.setNoteCollects(Long.valueOf(object.substring(2)),collectNumber);
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

}
