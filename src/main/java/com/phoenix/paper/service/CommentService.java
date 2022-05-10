package com.phoenix.paper.service;


import com.phoenix.paper.common.Page;
import com.phoenix.paper.entity.Comment;

public interface CommentService {

    void deleteComment(Long commentId,Long userId);

    void addComment(Long objectId,Integer objectType,Long userId,String content);

    Page<Comment> getCommentList(Long objectId, Integer objectType, Integer pageSize, Integer pageNum);
}
