package com.phoenix.read.service.impl;

import com.phoenix.read.entity.Collection;
import com.phoenix.read.entity.Like;
import com.phoenix.read.mapper.CollectionMapper;
import com.phoenix.read.mapper.LikeMapper;
import com.phoenix.read.service.CollectionService;
import com.phoenix.read.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private TimeUtil timeUtil;

    @Override
    public Long collect(Long passageId,Long userId){
        Collection collection=new Collection(null,userId,passageId,timeUtil.getCurrentTimestamp());
        collectionMapper.insert(collection);
        return collection.getId();
    }

    @Override
    public  String cancelCollection(Long id){
        collectionMapper.deleteByPrimaryKey(id);
        return "取消收藏成功";
    }
}
