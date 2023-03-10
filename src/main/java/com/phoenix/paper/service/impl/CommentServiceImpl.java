package com.phoenix.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.common.Page;
import com.phoenix.paper.dto.BriefComment;
import com.phoenix.paper.dto.SessionData;
import com.phoenix.paper.entity.Comment;
import com.phoenix.paper.entity.Note;
import com.phoenix.paper.entity.User;
import com.phoenix.paper.mapper.CommentMapper;
import com.phoenix.paper.mapper.NoteMapper;
import com.phoenix.paper.mapper.UserMapper;
import com.phoenix.paper.service.CommentService;
import com.phoenix.paper.util.AssertUtil;
import com.phoenix.paper.util.SessionUtils;
import com.phoenix.paper.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private SessionUtils sessionUtils;

    @Transactional(rollbackFor = CommonException.class)
    @Override
    public void deleteComment(Long commentId, Long userId) throws CommonException {
        Comment comment = commentMapper.selectById(commentId);
        User user = userMapper.selectById(userId);
        if (!comment.getUserId().equals(userId) && user.getType() != 1 && user.getCanComment() != 1)
            throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
        String deleteTime = TimeUtil.getCurrentTimestamp();
        comment.setDeleteTime(deleteTime);
        commentMapper.updateById(comment);
        if (comment.getNoteId() != null) {
            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.eq("comment_id",commentId);
//            Integer count = commentMapper.selectCount(commentQueryWrapper);
            commentMapper.update(Comment.builder().deleteTime(deleteTime).build(),commentQueryWrapper);
        }
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    public void addComment(Long objectId, Integer objectType, Long userId, String content) throws CommonException {
        if (sessionUtils.getSessionData().getCanComment() != 1)
            throw new CommonException(CommonErrorCode.CAN_NOT_COMMENT);
        if (objectType == 1) {
            Comment comment = commentMapper.selectById(objectId);
            if (comment == null || comment.getDeleteTime() != null)
                throw new CommonException(CommonErrorCode.COMENT_NOT_EXIST);
            if (comment.getCommentId() != null) throw new CommonException(CommonErrorCode.COMMENT_IS_NOT_ALLOWED);
        } else {
            Note note = noteMapper.selectById(objectId);
            if (note == null || note.getDeleteTime() != null) throw new CommonException(CommonErrorCode.NOTE_NOT_EXIST);
        }
        Comment comment = Comment.builder().userId(userId).createTime(TimeUtil.getCurrentTimestamp()).contents(content).version(1).build();
        if (objectType == 0) comment.setNoteId(objectId);
        else if (objectType == 1) comment.setCommentId(objectId);
        commentMapper.insert(comment);
    }

    @Override
    public Page<BriefComment> getCommentList(Long objectId, Integer objectType, Integer pageSize, Integer pageNum) {
        SessionData sessionData = sessionUtils.getSessionData();
        AssertUtil.isTrue(sessionData.getCanComment() == 1 || sessionData.getType() == 1, CommonErrorCode.CAN_NOT_COMMENT);
        if (objectType == 0) {
            PageHelper.startPage(pageNum, pageSize, "create_time desc");
            return new Page<>(new PageInfo<>(commentMapper.getNoteCommentList(objectId)));
        } else if (objectType == 1) {
            PageHelper.startPage(pageNum, pageSize, "create_time desc");
            return new Page<>(new PageInfo<>(commentMapper.getSecondLevelCommentList(objectId)));
        }
        return new Page<>(new PageInfo<>());
    }

}
