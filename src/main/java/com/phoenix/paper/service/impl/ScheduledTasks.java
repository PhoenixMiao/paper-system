package com.phoenix.paper.service.impl;



import com.phoenix.paper.entity.Likes;
import com.phoenix.paper.entity.Paper;
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

    @Scheduled(cron = "0 0 0/2 * * ? ")
    public void likes2database() {
        Map<Object,Object> likeInformation = new HashMap<>();
        likeInformation=redisUtils.hmget("LIKE_INFORMATION");
        for (Map.Entry<Object, Object> entry : likeInformation.entrySet()) {
            String information = (String) entry.getKey();
            String[] splitInfo = information.split(" ");
            String time = (String) entry.getValue();
            if(splitInfo[2].equals("1")){
                Likes like= Likes.builder().objectId(Long.valueOf(splitInfo[1].substring(1))).objectType(Integer.valueOf(splitInfo[1].charAt(0))).userId(Long.valueOf(splitInfo[0])).likeTime(time).build();
                likesMapper.insert(like);
            }
            else if(splitInfo[2].equals("0")){
                likesMapper.cancelLike(time,Long.valueOf(splitInfo[1].substring(1)),Integer.valueOf(splitInfo[1].charAt(0)));
            }
        }
        Map<Object,Object> likeCount = new HashMap<>();
        likeCount=redisUtils.hmget("LIKE_COUNT");
        for (Map.Entry<Object, Object> entry : likeCount.entrySet()){
            String object=(String) entry.getKey();
            Long likeNumber=(Long) entry.getValue();
            if(object.charAt(0)=='1'){
                paperMapper.setPaperLikes(Long.valueOf(object.substring(1)),likeNumber);
            }
            else if(object.charAt(0)=='0'){
                noteMapper.setNoteLikes(Long.valueOf(object.substring(1)),likeNumber);
            }
        }

        redisUtils.clear("LIKE_COUNT");
        redisUtils.clear("LIKE_INFORMATION");
    }

//    @Scheduled(cron = "0 0 0 * * ?")
////    @Scheduled(cron = "0 0 0 */1 * ?")
//    public void collection2Mysql() {
//        Set<String> keySet = redisUtils.keys("collections*");
//        for (String key : keySet) {
//            displayProjectMapper.setCollectionNumber(Long.valueOf(String.valueOf(redisUtils.get(key))),Long.valueOf(key.substring(11)));
//        }
//        redisUtils.clear("collections*");
//    }

}
