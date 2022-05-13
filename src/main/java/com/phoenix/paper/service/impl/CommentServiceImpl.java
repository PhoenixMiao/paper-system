package com.phoenix.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.common.Page;
import com.phoenix.paper.dto.BriefComment;
import com.phoenix.paper.entity.Comment;
import com.phoenix.paper.entity.User;
import com.phoenix.paper.mapper.CommentMapper;
import com.phoenix.paper.mapper.UserMapper;
import com.phoenix.paper.service.CommentService;
import com.phoenix.paper.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void deleteComment(Long commentId,Long userId) throws CommonException{
        Comment comment = commentMapper.selectById(commentId);
        User user = userMapper.selectById(userId);
        if(!comment.getUserId().equals(userId) && user.getType()!=1) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
        String deleteTime = TimeUtil.getCurrentTimestamp();
        comment.setDeleteTime(deleteTime);
        commentMapper.updateById(comment);
        if(comment.getNoteId()!=null){
            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.eq("comment_id",commentId);
//            Integer count = commentMapper.selectCount(commentQueryWrapper);
            commentMapper.update(Comment.builder().deleteTime(deleteTime).build(),commentQueryWrapper);
        }
    }

    @Override
    public void addComment(Long objectId,Integer objectType,Long userId,String content){
        if(objectType==1){
            Comment comment=commentMapper.selectById(objectId);
            if(comment.getCommentId()!=null)throw new CommonException(CommonErrorCode.COMMENT_IS_NOT_ALLOWED);
        }
        Comment comment=Comment.builder().userId(userId).createTime(TimeUtil.getCurrentTimestamp()).contents(content).build();
        if(objectType==0)comment.setNoteId(objectId);
        else if(objectType==1)comment.setCommentId(objectId);
        commentMapper.insert(comment);
    }

    @Override
    public Page<BriefComment> getCommentList(Long objectId, Integer objectType, Integer pageSize, Integer pageNum){
        if(objectType==0){
            PageHelper.startPage(pageNum,pageSize,"create_time desc");
            return new Page<>(new PageInfo<>(commentMapper.getNoteCommentList(objectId)));
        }
        else if(objectType==1){
            PageHelper.startPage(pageNum,pageSize,"create_time desc");
            return new Page<>(new PageInfo<>(commentMapper.getSecondLevelCommentList(objectId)));
        }
        return new Page<>(new PageInfo<>());
    }

}
