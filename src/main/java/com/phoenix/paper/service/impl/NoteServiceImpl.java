package com.phoenix.paper.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.paper.common.*;
import com.phoenix.paper.entity.Note;
import com.phoenix.paper.entity.Paper;
import com.phoenix.paper.mapper.NoteMapper;
import com.phoenix.paper.mapper.PaperMapper;
import com.phoenix.paper.service.NoteService;
import com.phoenix.paper.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class NoteServiceImpl implements NoteService{

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private PaperMapper paperMapper;

    @Override
    public String uploadNote(MultipartFile file,Long noteId) throws CommonException {
        Note note = noteMapper.selectByPrimaryKey(noteId);
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
        noteMapper.updateByPrimaryKeySelective(Note.builder().id(noteId).noteLink(link).build());
        return link;
    }

    @Override
    public Long addNote(Long authorId,Long paperId) throws CommonException{
        Paper paper = paperMapper.selectByPrimaryKey(paperId);
        if(paper == null || paper.getDeleteTime()!=null) throw new CommonException(CommonErrorCode.PAPER_NOT_EXIST);
        Note note = Note.builder()
                .authorId(authorId)
                .createTime(TimeUtil.getCurrentTimestamp())
                .paperId(paperId)
                .build();
        noteMapper.insert(note);
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
}
