package com.phoenix.paper.service.impl;

import com.phoenix.paper.mapper.CommentMapper;
import com.phoenix.paper.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;


}
