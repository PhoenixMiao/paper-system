package com.phoenix.paper.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.phoenix.paper.common.Page;
import com.phoenix.paper.dto.BriefComment;
import com.phoenix.paper.entity.Collection;
import com.phoenix.paper.entity.Comment;
import io.swagger.models.auth.In;

public interface CommentService {

    void addComment(Long objectId,Integer objectType,Long userId,String content);

    void deleteComment(Long commentId);

    Page<Comment> getCommentList(Long objectId, Integer objectType,Integer pageSize,Integer pageNum);
}
