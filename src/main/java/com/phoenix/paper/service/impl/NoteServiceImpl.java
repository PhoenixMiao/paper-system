package com.phoenix.paper.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.paper.common.*;
import com.phoenix.paper.controller.request.SearchNoteRequest;
import com.phoenix.paper.dto.BriefNote;
import com.phoenix.paper.entity.*;
import com.phoenix.paper.mapper.*;
import com.phoenix.paper.service.NoteService;
import com.phoenix.paper.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoteServiceImpl implements NoteService{

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private LikesMapper likesMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Override
    public String uploadNote(MultipartFile file,Long noteId) throws CommonException {
        Note note = noteMapper.selectById(noteId);
        if(note==null || note.getDeleteTime()!=null) throw new CommonException(CommonErrorCode.NOTE_NOT_EXIST);
        String originalFilename = file.getOriginalFilename();
        String flag = IdUtil.fastSimpleUUID();
        String rootFilePath = System.getProperty("user.dir") + "/src/main/resources/files/" + flag + "-" + originalFilename;
        try{
            FileUtil.writeBytes(file.getBytes(),rootFilePath);
        }catch (IOException e){
            throw new CommonException(CommonErrorCode.READ_FILE_ERROR);
        }
        String link = CommonConstants.DOWNLOAD_PATH + flag;
        note.setNoteLink(link);
        if(noteMapper.updateById(note)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
        return link;
    }

    @Override
    public void updateNote(MultipartFile file,Long noteId) throws CommonException{
        Note note = noteMapper.selectById(noteId);
        if(note==null || note.getDeleteTime()!=null) throw new CommonException(CommonErrorCode.NOTE_NOT_EXIST);
        String originalFilename = file.getOriginalFilename();
        String flag = IdUtil.fastSimpleUUID();
        String rootFilePath = System.getProperty("user.dir") + "/src/main/resources/files/" + flag + "-" + originalFilename;
        try{
            FileUtil.writeBytes(file.getBytes(),rootFilePath);
        }catch (IOException e){
            throw new CommonException(CommonErrorCode.READ_FILE_ERROR);
        }
        String link = CommonConstants.DOWNLOAD_PATH + flag;
        note.setNoteLink(link);
        if(noteMapper.updateById(note)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
    }

    @Override
    public Long addNote(Long authorId,Long paperId) throws CommonException{
        Paper paper = paperMapper.selectById(paperId);
        if(paper == null || paper.getDeleteTime()!=null) throw new CommonException(CommonErrorCode.PAPER_NOT_EXIST);
        Note note = Note.builder()
                .authorId(authorId)
                .createTime(TimeUtil.getCurrentTimestamp())
                .paperId(paperId)
                .build();
        noteMapper.insert(note);
        User user = userMapper.selectById(authorId);
        user.setNoteNum(user.getNoteNum()+1);
        user.setNoteWeekNum(user.getNoteWeekNum()+1);
        if(userMapper.updateById(user)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
        return note.getId();
    }

    @Override
    public Page<Note> getNoteList(int pageSize, int pageNum, int orderBy){
        if(orderBy == 0){
            PageHelper.startPage(pageNum,pageSize,"create_time desc");
        }else{
            PageHelper.startPage(pageNum,pageSize,"like_number+collect_number desc");
        }
        return new Page<>(new PageInfo<>(noteMapper.getNoteList()));
    }

    @Override
    public  Page<BriefNote> searchNote(SearchNoteRequest searchNoteRequest){
        return new Page<>(new PageInfo<>());
    }

    @Override
    public Note getNoteDetails(Long noteId) throws CommonException{
        Note note=noteMapper.selectById(noteId);
        if(note==null || note.getDeleteTime()!=null)throw new CommonException(CommonErrorCode.NOTE_NOT_EXIST);
        return note;
    }

    @Override
    public void deleteNote(Long noteId,Long userId)throws CommonException{
        Note note = noteMapper.selectById(noteId);
        User user = userMapper.selectById(userId);
        if(!note.getAuthorId().equals(userId) && user.getType()!= 1) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
        String deleteTime = TimeUtil.getCurrentTimestamp();
        note.setDeleteTime(deleteTime);
        if(noteMapper.updateById(note)==0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
        QueryWrapper<Likes> likesQueryWrapper = new QueryWrapper<>();
        likesQueryWrapper.eq("object_id",note.getId()).eq("object_type",1);
        if(likesMapper.update(Likes.builder().deleteTime(deleteTime).build(),likesQueryWrapper)==0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
        QueryWrapper<Collection> collectionQueryWrapper = new QueryWrapper<>();
        collectionQueryWrapper.eq("object_id",note.getId()).eq("object_type",1);
        if(collectionMapper.update(Collection.builder().deleteTime(deleteTime).build(), collectionQueryWrapper)==0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("object_id",note.getId()).eq("object_type",0);
        List<Comment> comments = commentMapper.selectList(commentQueryWrapper);
        for (Comment comment : comments) {
            QueryWrapper<Comment> commentQueryWrapper1 = new QueryWrapper<>();
            commentQueryWrapper1.eq("object_id",comment.getId()).eq("object_type",1);
            if(commentMapper.update(Comment.builder().deleteTime(deleteTime).build(), commentQueryWrapper1)==0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
        }
        if(commentMapper.update(Comment.builder().deleteTime(deleteTime).build(), commentQueryWrapper)==0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
    }

}
