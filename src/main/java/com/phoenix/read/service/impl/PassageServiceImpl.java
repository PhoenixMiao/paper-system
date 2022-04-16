package com.phoenix.read.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.read.common.CommonErrorCode;
import com.phoenix.read.common.CommonException;
import com.phoenix.read.common.Page;
import com.phoenix.read.controller.request.GetPassageListRequest;
import com.phoenix.read.controller.request.PostRequest;
import com.phoenix.read.controller.request.SearchRequest;
import com.phoenix.read.controller.response.GetPassageResponse;
import com.phoenix.read.dto.BriefPassage;
import com.phoenix.read.entity.Passage;
import com.phoenix.read.mapper.CommentMapper;
import com.phoenix.read.mapper.PassageMapper;
import com.phoenix.read.mapper.UserMapper;
import com.phoenix.read.service.PassageService;
import com.phoenix.read.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PassageServiceImpl implements PassageService {

    @Autowired
    private PassageMapper passageMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TimeUtil timeUtil;

    @Override
    public Page<BriefPassage> searchPassage(SearchRequest searchRequest){
        Example exampleOne = new Example(Passage.class);

        if (!StringUtils.isEmpty(searchRequest.getTitle())) {
            Example.Criteria nameCriteria = exampleOne.createCriteria();
            nameCriteria.orLike("title", "%" + searchRequest.getTitle() + "%");
            exampleOne.and(nameCriteria);
        }

        PageHelper.startPage(searchRequest.getPageParam().getPageNum(),
                searchRequest.getPageParam().getPageSize(),
                searchRequest.getPageParam().getOrderBy());
        List<Passage> passageListOne = passageMapper.selectByExample(exampleOne);

        Example exampleTwo = new Example(Passage.class);

        if (!StringUtils.isEmpty(searchRequest.getContent())) {
            Example.Criteria nameCriteria = exampleTwo.createCriteria();
            nameCriteria.orLike("content", "%" + searchRequest.getContent() + "%");
            exampleTwo.and(nameCriteria);
        }

        PageHelper.startPage(searchRequest.getPageParam().getPageNum(),
                searchRequest.getPageParam().getPageSize(),
                searchRequest.getPageParam().getOrderBy());
        List<Passage> passageListTwo = passageMapper.selectByExample(exampleTwo);

        List<Passage> allPassageList=passageListOne;
        allPassageList.addAll(passageListTwo);
        PageHelper.startPage(searchRequest.getPageParam().getPageNum(),
                searchRequest.getPageParam().getPageSize(),
                searchRequest.getPageParam().getOrderBy());
        List<Passage> passageListWithoutDuplicates = allPassageList.stream().distinct().collect(Collectors.toList());
        Page page = new Page(new PageInfo(passageListWithoutDuplicates));

        ArrayList<BriefPassage> responseList = new ArrayList<>();
        for (Passage ele : passageListWithoutDuplicates) {
            responseList.add(new BriefPassage(ele.getId(), ele.getUserId(), ele.getContent(), ele.getTitle()));
        }
        return new Page<>(searchRequest.getPageParam(), page.getTotal(), page.getPages(), responseList);
    }

    @Override
    public Page<Passage> getPassageList(GetPassageListRequest getPassageListRequest){
        PageHelper.startPage(getPassageListRequest.getPageParam().getPageNum(),getPassageListRequest.getPageParam().getPageSize(),getPassageListRequest.getPageParam().getOrderBy());
        if(getPassageListRequest.getOrder().equals(2)) return new Page(new PageInfo<>(passageMapper.getPassageListByHot(getPassageListRequest.getType(),getPassageListRequest.getSubtype())));
        else return new Page(new PageInfo<>(passageMapper.getPassageListByTime(getPassageListRequest.getType(),getPassageListRequest.getSubtype())));
    }

    @Override
    public GetPassageResponse getPassageById(Long passageId){
        List<Long> commentsId=commentMapper.getCommentList(passageId,0);
        Passage passage=passageMapper.selectByPrimaryKey(passageId);
        return new GetPassageResponse(passage,commentsId);
    }

    @Override
    public Long postNewPassage(PostRequest postRequest,Long userId){
        if(userMapper.selectByPrimaryKey(userId).getIsMute()==1) throw new CommonException(CommonErrorCode.USER_IS_MUTE);
        Passage passage=new Passage(null,postRequest.getType(),postRequest.getSubtype(),userId,postRequest.getContent(),timeUtil.getCurrentTimestamp(),postRequest.getTitle());
        return (long) passageMapper.insert(passage);
    }
}
