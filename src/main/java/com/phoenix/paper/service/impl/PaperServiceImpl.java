package com.phoenix.paper.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.paper.common.CommonConstants;
import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.common.Page;
import com.phoenix.paper.controller.request.AddPaperRequest;
import com.phoenix.paper.controller.response.GetUserPaperListResponse;
import com.phoenix.paper.dto.BriefPaper;
import com.phoenix.paper.entity.Note;
import com.phoenix.paper.entity.Paper;
import com.phoenix.paper.entity.User;
import com.phoenix.paper.mapper.PaperMapper;
import com.phoenix.paper.mapper.UserMapper;
import com.phoenix.paper.service.PaperService;
import com.phoenix.paper.util.TimeUtil;
import org.apache.ibatis.annotations.Param;
import org.elasticsearch.common.recycler.Recycler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.index.PathBasedRedisIndexDefinition;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Time;

@Service
public class PaperServiceImpl implements PaperService {

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Paper getPaperById(Long paperId){
        return paperMapper.selectById(paperId);
    }

    @Override
    public Page<BriefPaper> getPaperList(int pageNum, int pageSize, int orderBy){
        if(orderBy == 0){
            PageHelper.startPage(pageNum,pageSize,"paper_date desc");
        }else{
            PageHelper.startPage(pageNum,pageSize,"like_number+collect_number desc");
        }
        return new Page<>(new PageInfo<>(paperMapper.getPaperList()));

    }

    @Override
    public GetUserPaperListResponse getUserPaperList(Integer pageNum, Integer pageSize, Long userId){
        User user=userMapper.selectById(userId);
        if(user==null||user.getDeleteTime()!=null)throw new CommonException(CommonErrorCode.USER_NOT_EXIST);
        PageHelper.startPage(pageNum,pageSize,"upload_time desc");
        return new GetUserPaperListResponse(paperMapper.getUserTotalPaperNumber(userId),paperMapper.getUserPaperNumberInThisWeek(userId),new Page<>(new PageInfo<>(paperMapper.getUserPaperList(userId))));
    }

    @Override
    public Long addPaper(Long userId) throws CommonException{
        Paper paper=Paper.builder().uploaderId(userId).build();
        paperMapper.insert(paper);
        User user = userMapper.selectById(userId);
        user.setPaperWeekNum(user.getPaperWeekNum()+1);
        user.setPaperNum(user.getPaperWeekNum()+1);
        if(userMapper.updateById(user)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
        return paper.getId();
    }

    @Override
    public  String uploadPaper(MultipartFile file, Long paperId)throws CommonException{
        Paper paper = paperMapper.selectById(paperId);
        if(paper==null || paper.getDeleteTime()!=null) throw new CommonException(CommonErrorCode.PAPER_NOT_EXIST);
        String originalFilename = file.getOriginalFilename();
        String flag = IdUtil.fastSimpleUUID();
        String rootFilePath = System.getProperty("user.dir") + "/src/main/resources/files/" + flag + "-" + originalFilename;
        try{
            FileUtil.writeBytes(file.getBytes(),rootFilePath);
        }catch (IOException e){
            throw new CommonException(CommonErrorCode.READ_FILE_ERROR);
        }
        String link = CommonConstants.DOWNLOAD_PATH + flag;
        paper.setFileLink(link);
        if(paperMapper.updateById(paper)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
        return link;
    }
}
