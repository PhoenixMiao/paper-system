package com.phoenix.read.service.impl;

import com.phoenix.read.common.CommonErrorCode;
import com.phoenix.read.common.CommonException;
import com.phoenix.read.controller.request.CommentRequest;
import com.phoenix.read.entity.Comment;
import com.phoenix.read.mapper.CommentMapper;
import com.phoenix.read.mapper.UserMapper;
import com.phoenix.read.service.CommentService;
import com.phoenix.read.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private TimeUtil timeUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Comment getCommentById(Long id){
        Comment comment=commentMapper.selectByPrimaryKey(id);
        return comment;
    }

    @Override
    public List<Long> getCommentsIdByIdAndType (Long commentId,Integer commentType){
        return commentMapper.getCommentList(commentId,commentType);
    }

    @Override
    public Long addComment(CommentRequest commentRequest,Long userId){
        if(commentRequest.getObjectType().equals(1)){
            Comment comment=commentMapper.selectByPrimaryKey(commentRequest.getObjectId());
            if(comment.getObjectType().equals(1)){
                throw new CommonException(CommonErrorCode.COMMENT_IS_NOT_ALLOWED);
            }
        }
        if(userMapper.selectByPrimaryKey(userId).getIsMute()==1){
            throw new CommonException(CommonErrorCode.USER_IS_MUTE);
        }
        return commentMapper.addComment(commentRequest.getObjectId(),commentRequest.getObjectType(),userId,timeUtil.getCurrentTimestamp(),commentRequest.getComment());
    }
}
