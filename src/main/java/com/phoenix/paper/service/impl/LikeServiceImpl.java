package com.phoenix.paper.service.impl;

import com.phoenix.paper.mapper.NoteMapper;
import com.phoenix.paper.mapper.PaperMapper;
import com.phoenix.paper.service.LikeService;
import com.phoenix.paper.util.ShuaiDatabaseUtils;
import com.phoenix.paper.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private ShuaiDatabaseUtils shuaiDatabaseUtils;

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private NoteMapper noteMapper;

    private String LIKE_INFORMATION_KEY(Long userId,Integer objectType,Long objectId,Integer status) {
        return userId+" "+objectType+objectId+" "+status;
    }

    private String LIKE_COUNT_KEY(Integer objectType,Long objectId ) {
        return objectType+" "+objectId;
    }

    private Long getLikesFromRedis(Long objectId, Integer type) {
        try {
            return (Long) shuaiDatabaseUtils.hget("LIKE_COUNT", LIKE_COUNT_KEY(type, objectId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public Long getLikeNumber(Long objectId, Integer type) {
        Long likes = getLikesFromRedis(objectId, type);
        if (likes != null) return likes;
        else if (type == 0) likes = Optional.ofNullable(paperMapper.getPaperLikes(objectId)).orElse(0L);
        else if (type == 1) likes = Optional.ofNullable(noteMapper.getNoteLikes(objectId)).orElse(0L);
        Map<String, String> likeCount = new HashMap<>();
        if (likes != null) likeCount.put(LIKE_COUNT_KEY(type, objectId), likes.toString());
        shuaiDatabaseUtils.hmset("LIKE_COUNT", likeCount);
        return likes;
    }


    @Override
    public Long like(Long objectId, Integer type, Long userId) {
        Map<String, String> likeInformation = new HashMap<>();
        likeInformation.put(LIKE_INFORMATION_KEY(userId, type, objectId, 1), TimeUtil.getCurrentTimestamp());
        shuaiDatabaseUtils.hmset("LIKE_INFORMATION", likeInformation);


        long likeNumber = getLikeNumber(objectId, type) + 1;
        Map<String, String> likeCount = new HashMap<>();
        likeCount.put(LIKE_COUNT_KEY(type, objectId), String.valueOf(likeNumber));
        shuaiDatabaseUtils.hmset("LIKE_COUNT", likeCount);
        return likeNumber + 1;
    }

    @Override
    public Long cancelLike(Long objectId, Integer type, Long userId) {
        Map<String, String> likeInformation = new HashMap<>();
        likeInformation.put(LIKE_INFORMATION_KEY(userId, type, objectId, 0), TimeUtil.getCurrentTimestamp());
        shuaiDatabaseUtils.hmset("LIKE_INFORMATION", likeInformation);


        long likeNumber = getLikeNumber(objectId, type) - 1;
        Map<String, String> likeCount = new HashMap<>();
        likeCount.put(LIKE_COUNT_KEY(type, objectId), String.valueOf(likeNumber));
        shuaiDatabaseUtils.hmset("LIKE_COUNT", likeCount);
        return likeNumber - 1;
    }

}
