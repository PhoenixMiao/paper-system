package com.phoenix.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.common.Page;
import com.phoenix.paper.dto.BriefComment;
import com.phoenix.paper.entity.Collection;
import com.phoenix.paper.entity.Comment;
import com.phoenix.paper.mapper.CommentMapper;
import com.phoenix.paper.service.CommentService;
import com.phoenix.paper.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public void addComment(Long objectId,Integer objectType,Long userId,String content){
        Comment comment=Comment.builder().objectType(objectType).objectId(objectId).userId(userId).createTime(TimeUtil.getCurrentTimestamp()).contents(content).build();
        commentMapper.insert(comment);
    }

    @Override
    public void deleteComment(Long commentId){
        Comment comment=commentMapper.selectById(commentId);
        if(comment==null || comment.getDeleteTime()!=null)throw new CommonException(CommonErrorCode.COMMENT_NOT_EXIST);
        comment.setDeleteTime(TimeUtil.getCurrentTimestamp());
        commentMapper.updateById(comment);
    }

    @Override
    public Page<Comment> getCommentList(Long objectId, Integer objectType,Integer pageSize,Integer pageNum){
        if(objectType==0 || objectType==1){
            PageHelper.startPage(pageNum,pageSize,"create_time desc");
            return new Page<>(new PageInfo<>(commentMapper.getCommentList(objectId,objectType)));
        }
        else if(objectType==2){
            PageHelper.startPage(pageNum,pageSize,"create_time desc");
            return new Page<>(new PageInfo<>(commentMapper.getMultilevelCommentList(objectId.toString())));
        }
        return new Page<>(new PageInfo<>());
    }
}
