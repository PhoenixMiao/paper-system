package com.phoenix.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.phoenix.paper.entity.Collection;
import com.phoenix.paper.mapper.CollectionMapper;
import com.phoenix.paper.mapper.NoteMapper;
import com.phoenix.paper.mapper.PaperMapper;
import com.phoenix.paper.service.CollectionService;
import com.phoenix.paper.util.ShuaiDatabaseUtils;
import com.phoenix.paper.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private ShuaiDatabaseUtils shuaiDatabaseUtils;

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private ScheduledTasks scheduledTasks;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    private String COLLECT_INFORMATION_KEY(Long userId,Integer objectType,Long objectId,Integer status) {
        return userId+" "+objectType+objectId+" "+status;
    }

    private String COLLECT_COUNT_KEY(Integer objectType,Long objectId ) {
        return objectType+" "+objectId;
    }

    private Long getCollectionsFromRedis(Long objectId, Integer type) {
        try {
            return (Long) shuaiDatabaseUtils.hget("COLLECT_COUNT", COLLECT_COUNT_KEY(type, objectId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public Long getCollectNumber(Long objectId, Integer type) {
        Long collects = getCollectionsFromRedis(objectId, type);
        if (collects != null) return collects;
        else if (type == 0) collects = Optional.ofNullable(paperMapper.getPaperCollects(objectId)).orElse(0L);
        else if (type == 1) collects = Optional.ofNullable(noteMapper.getNoteCollects(objectId)).orElse(0L);
        Map<String, String> collectCount = new HashMap<>();
        if (collects != null) collectCount.put(COLLECT_COUNT_KEY(type, objectId), collects.toString());
        shuaiDatabaseUtils.hmset("COLLECT_COUNT", collectCount);
        return collects;
    }


    @Override
    public Long collect(Long objectId, Integer type, Long userId) {
        Map<String, String> collectInformation = new HashMap<>();
        collectInformation.put(COLLECT_INFORMATION_KEY(userId, type, objectId, 1), TimeUtil.getCurrentTimestamp());
        shuaiDatabaseUtils.hmset("COLLECT_INFORMATION", collectInformation);


        long collectNumber = getCollectNumber(objectId, type) + 1;
        Map<String, String> collectCount = new HashMap<>();
        collectCount.put(COLLECT_COUNT_KEY(type, objectId), Long.toString(collectNumber));
        shuaiDatabaseUtils.hmset("COLLECT_COUNT", collectCount);
        return collectNumber + 1;
    }

    @Override
    public Long cancelCollect(Long objectId, Integer type, Long userId) {
        Map<String, String> collectInformation = new HashMap<>();
        collectInformation.put(COLLECT_INFORMATION_KEY(userId, type, objectId, 0), TimeUtil.getCurrentTimestamp());
        shuaiDatabaseUtils.hmset("COLLECT_INFORMATION", collectInformation);


        long collectNumber = getCollectNumber(objectId, type) - 1;
        Map<String, String> collectCount = new HashMap<>();
        collectCount.put(COLLECT_COUNT_KEY(type, objectId), String.valueOf(collectNumber));
        shuaiDatabaseUtils.hmset("COLLECT_COUNT", collectCount);
        return collectNumber - 1;
    }

    @Override
    public IPage<Collection> getCollectionList(Integer pageSize, Integer pageNum, Long userId){
        scheduledTasks.collections2database();
        QueryWrapper<Collection> collectionQueryWrapper = new QueryWrapper<>();
        collectionQueryWrapper.eq("user_id",userId).isNull("delete_time");
        collectionQueryWrapper.select("id","object_id","object_type","user_id","collect_time");
        collectionQueryWrapper.orderByAsc("collect_time");
        return collectionMapper.selectPage(new Page<Collection>(pageNum,pageSize),collectionQueryWrapper);
    }
}
