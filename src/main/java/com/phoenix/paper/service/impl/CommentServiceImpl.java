package com.phoenix.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.common.Page;
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
        if(comment.getObjectType()==0){
            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.eq("object_id",commentId).eq("object_type",1);
            if(commentMapper.update(Comment.builder().deleteTime(deleteTime).build(),commentQueryWrapper)==0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
        }
    }

    @Override
    public void addComment(Long objectId,Integer objectType,Long userId,String content){
        Comment comment=Comment.builder().objectType(objectType).objectId(objectId).userId(userId).createTime(TimeUtil.getCurrentTimestamp()).contents(content).build();
        commentMapper.insert(comment);
    }

    @Override
    public Page<Comment> getCommentList(Long objectId, Integer objectType, Integer pageSize, Integer pageNum){
        if(objectType==0 || objectType==1){
            PageHelper.startPage(pageNum,pageSize,"create_time desc");
            return new Page<>(new PageInfo<>(commentMapper.getCommentList(objectId,objectType)));
        }
//        else if(objectType==2){
//        }
        return new Page<>(new PageInfo<>());
    }

}
