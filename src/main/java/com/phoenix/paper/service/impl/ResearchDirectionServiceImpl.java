package com.phoenix.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.controller.request.AddResearchDirectionRequest;
import com.phoenix.paper.dto.BriefNode;
import com.phoenix.paper.entity.*;
import com.phoenix.paper.mapper.*;
import com.phoenix.paper.service.ResearchDirectionService;
import com.phoenix.paper.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResearchDirectionServiceImpl implements ResearchDirectionService {

    @Autowired
    private ResearchDirectionMapper researchDirectionMapper;

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private LikesMapper likesMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PaperQuotationMapper paperQuotationMapper;

    @Autowired
    private PaperDirectionMapper paperDirectionMapper;


    @Override
    public Long addResearchDirection(AddResearchDirectionRequest addResearchDirectionRequest, Long creatorId) throws CommonException {
        QueryWrapper<ResearchDirection> researchDirectionQueryWrapper = new QueryWrapper<>();
        researchDirectionQueryWrapper.eq("name",addResearchDirectionRequest.getName());
        ResearchDirection researchDirectionOld = researchDirectionMapper.selectOne(researchDirectionQueryWrapper);
        if (researchDirectionOld == null || researchDirectionOld.getDeleteTime()!=null)
            throw new CommonException(CommonErrorCode.REPETITIVE_DIRECTION);
        ResearchDirection researchDirection;
        if (addResearchDirectionRequest.getFatherId() != 0) {
            ResearchDirection fatherResearchDirection = researchDirectionMapper.selectById(addResearchDirectionRequest.getFatherId());
            if(fatherResearchDirection.getIsLeaf()==1){
                fatherResearchDirection.setIsLeaf(0);
                if(researchDirectionMapper.updateById(fatherResearchDirection)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
            }
            synchronized (this) {
                QueryWrapper<ResearchDirection> researchDirectionQueryWrapper1 = new QueryWrapper<>();
                researchDirectionQueryWrapper1.eq("root_id",fatherResearchDirection.getRootId()).eq("father_id",fatherResearchDirection.getFatherId());
                int nodeNum = researchDirectionMapper.selectCount(researchDirectionQueryWrapper1);
                researchDirection = ResearchDirection.builder()
                        .fatherId(fatherResearchDirection.getId())
                        .createTime(TimeUtil.getCurrentTimestamp())
                        .creatorId(creatorId)
                        .name(addResearchDirectionRequest.getName())
                        .rootId(fatherResearchDirection.getRootId())
                        .path(fatherResearchDirection.getPath() + (new Integer(nodeNum)).toString())
                        .isLeaf(1)
                        .build();
                researchDirectionMapper.insert(researchDirection);
            }
        } else {
            synchronized (this) {
                researchDirection = ResearchDirection.builder()
                        .name(addResearchDirectionRequest.getName())
                        .path("0")
                        .createTime(TimeUtil.getCurrentTimestamp())
                        .creatorId(creatorId)
                        .fatherId((long) 0)
                        .isLeaf(1)
                        .build();
                researchDirectionMapper.insert(researchDirection);
                researchDirection.setRootId(researchDirection.getRootId());
                if(researchDirectionMapper.updateById(researchDirection)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
            }
        }
        return researchDirection.getId();
    }

    @Override
    public List<BriefNode> getSons(Long father) throws CommonException{
        if(father==0) return researchDirectionMapper.getSons((long)0);
        ResearchDirection fatherDirection = researchDirectionMapper.selectById(father);
        if(fatherDirection == null || fatherDirection.getDeleteTime()!=null) throw new CommonException(CommonErrorCode.RESEARCH_DIRECTION_NOT_EXIST);
        if(fatherDirection.getIsLeaf()==1) throw new CommonException(CommonErrorCode.HAVE_NO_SON);
        return researchDirectionMapper.getSons(father);
    }

    @Override
    public List<Long> getAllSons(Long father) throws CommonException{
        ResearchDirection researchDirection = researchDirectionMapper.selectById(father);
        if(researchDirection == null || researchDirection.getDeleteTime()!=null) throw new CommonException(CommonErrorCode.RESEARCH_DIRECTION_NOT_EXIST);
        return researchDirectionMapper.getAllSons(researchDirection.getRootId(),researchDirection.getPath()+'%');
    }

    @Override
    public void deleteNode(Long id) throws CommonException{
        List<Long> sonList = getAllSons(id);
        String deleteTime = TimeUtil.getCurrentTimestamp();
        for(Long node:sonList){
            ResearchDirection researchDirection = researchDirectionMapper.selectById(node);
            researchDirection.setDeleteTime(TimeUtil.getCurrentTimestamp());
            if(researchDirectionMapper.updateById(researchDirection)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
            QueryWrapper<PaperDirection> paperDirectionQueryWrapper = new QueryWrapper<>();
            paperDirectionQueryWrapper.eq("direction_id",id).select("paper_id");
            List<PaperDirection> paperDirections = paperDirectionMapper.selectList(paperDirectionQueryWrapper);
            for(PaperDirection paperDirection:paperDirections){
                Long paperId = paperDirection.getPaperId();
                QueryWrapper<Paper> paperQueryWrapper = new QueryWrapper<>();
                paperQueryWrapper.eq("id",paperId);
                List<Paper> papers = paperMapper.selectList(paperQueryWrapper);
                for(Paper paper:papers){
                    QueryWrapper<Note> noteQueryWrapper = new QueryWrapper<>();
                    noteQueryWrapper.eq("paper_id",paper.getId());
                    List<Note> notes = noteMapper.selectList(noteQueryWrapper);
                    for (Note note : notes) {
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
                    QueryWrapper<Likes> likesQueryWrapper = new QueryWrapper<>();
                    likesQueryWrapper.eq("object_id",paper.getId()).eq("object_type",0);
                    if(likesMapper.update(Likes.builder().deleteTime(deleteTime).build(), likesQueryWrapper)==0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
                    QueryWrapper<Collection> collectionQueryWrapper = new QueryWrapper<>();
                    collectionQueryWrapper.eq("object_id",paper.getId()).eq("object_type",0);
                    if(collectionMapper.update(Collection.builder().deleteTime(deleteTime).build(), collectionQueryWrapper)==0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
                    QueryWrapper<PaperQuotation> paperQuotationQueryWrapper = new QueryWrapper<>();
                    paperQuotationQueryWrapper.eq("paper_id",paper.getId());
                    if(paperQuotationMapper.update(PaperQuotation.builder().deleteTime(deleteTime).build(), paperQuotationQueryWrapper)==0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
                    QueryWrapper<PaperDirection> paperDirectionQueryWrapper1 = new QueryWrapper<>();
                    if(paperDirectionMapper.update(PaperDirection.builder().deleteTime(deleteTime).build(),paperDirectionQueryWrapper1)==0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);

                }
            }


        }
    }

    @Override
    public void updateNode(Long id,String name) throws CommonException{
        ResearchDirection researchDirection = researchDirectionMapper.selectById(id);
        if(researchDirection==null || researchDirection.getDeleteTime() != null) throw new CommonException(CommonErrorCode.RESEARCH_DIRECTION_NOT_EXIST);
        researchDirection.setName(name);
        if(researchDirectionMapper.updateById(researchDirection)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
    }
}
