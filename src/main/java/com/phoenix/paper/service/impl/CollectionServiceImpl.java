package com.phoenix.paper.service.impl;

import cn.hutool.core.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.paper.entity.Collection;
import com.phoenix.paper.mapper.CollectionMapper;
import com.phoenix.paper.mapper.NoteMapper;
import com.phoenix.paper.mapper.PaperMapper;
import com.phoenix.paper.service.CollectionService;
import com.phoenix.paper.util.RedisUtils;
import com.phoenix.paper.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PaperMapper paperMapper;

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
            return (Long )redisUtils.hget("COLLECT_COUNT",COLLECT_COUNT_KEY(type,objectId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public Long getCollectNumber(Long objectId, Integer type){
        Long collects = getCollectionsFromRedis(objectId,type);
        if (collects != null) return collects;
        else if(type==0)collects = Optional.ofNullable(paperMapper.getPaperCollects(objectId)).orElse(0L);
        else if(type==1)collects=Optional.ofNullable(noteMapper.getNoteCollects(objectId)).orElse(0L);
        Map<String,Object> collectCount=new HashMap<>();
        collectCount.put(COLLECT_COUNT_KEY(type,objectId),collects);
        redisUtils.hmset("COLLECT_COUNT",collectCount);
        return collects;
    }


    @Override
    public Long collect(Long objectId, Integer type, Long userId){
        Map<String,Object> collectInformation=new HashMap<>();
        collectInformation.put(COLLECT_INFORMATION_KEY(userId,type,objectId,1), TimeUtil.getCurrentTimestamp());
        redisUtils.hmset("COLLECT_INFORMATION",collectInformation);


        Long collectNumber = getCollectNumber(objectId,type);
        Map<String,Object> collectCount=new HashMap<>();
        collectCount.put(COLLECT_COUNT_KEY(type,objectId),collectNumber+1);
        redisUtils.hmset("COLLECT_COUNT", collectCount );
        return collectNumber+1;
    }

    @Override
    public Long cancelCollect(Long objectId, Integer type, Long userId) {
        Map<String,Object> collectInformation=new HashMap<>();
        collectInformation.put(COLLECT_INFORMATION_KEY(userId,type,objectId,0), TimeUtil.getCurrentTimestamp());
        redisUtils.hmset("COLLECT_INFORMATION",collectInformation);


        Long collectNumber = getCollectNumber(objectId,type);
        Map<String,Object> collectCount=new HashMap<>();
        collectCount.put(COLLECT_COUNT_KEY(type,objectId),collectNumber-1);
        redisUtils.hmset("COLLECT_COUNT", collectCount );
        return collectNumber-1;
    }

    @Override
    public IPage<Collection> getCollectionList(Integer pageSize, Integer pageNum, Long userId){
        QueryWrapper<Collection> collectionQueryWrapper = new QueryWrapper<>();
        collectionQueryWrapper.eq("user_id",userId).isNull("delete_time");
        collectionQueryWrapper.select("id","object_id","object_type","user_id","collect_time");
        collectionQueryWrapper.orderByAsc("collect_time");
        return collectionMapper.selectPage(new Page<Collection>(pageNum,pageSize),collectionQueryWrapper);
    }
}
