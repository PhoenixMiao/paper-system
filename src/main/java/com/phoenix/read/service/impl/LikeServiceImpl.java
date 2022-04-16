package com.phoenix.read.service.impl;

import com.phoenix.read.common.CommonErrorCode;
import com.phoenix.read.common.CommonException;
import com.phoenix.read.entity.Like;
import com.phoenix.read.mapper.LikeMapper;
import com.phoenix.read.service.LikeService;
import com.phoenix.read.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeMapper likeMapper;

    @Override
    public Long like(Long passageId,Long userId) throws CommonException {
        Like like = likeMapper.isLike(userId,passageId);
        if(like==null) {
            Like like2 = new Like(null, userId, passageId, TimeUtil.getCurrentTimestamp());
            likeMapper.insert(like2);
            return like2.getId();
        }else{
            throw new CommonException(CommonErrorCode.HAS_LIKED);
        }

    }

    @Override
    public  String cancelLike(Long id){
        likeMapper.deleteByPrimaryKey(id);
        return "取消成功";
    }

    @Override
    public Integer isLike(Long userId,Long passageId){
        if(likeMapper.isLike(userId,passageId)!=null) return 1;
        else return 0;
    }
}
