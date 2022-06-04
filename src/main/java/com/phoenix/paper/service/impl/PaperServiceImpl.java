package com.phoenix.paper.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.phoenix.paper.common.CommonConstants;
import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.common.Page;
import com.phoenix.paper.controller.request.AddPaperRequest;
import com.phoenix.paper.controller.request.SearchPaperRequest;
import com.phoenix.paper.controller.request.UpdatePaperRequest;
import com.phoenix.paper.dto.BriefPaper;
import com.phoenix.paper.dto.DetailedPaper;
import com.phoenix.paper.dto.SearchPaper;
import com.phoenix.paper.dto.SessionData;
import com.phoenix.paper.entity.Collection;
import com.phoenix.paper.entity.*;
import com.phoenix.paper.mapper.*;
import com.phoenix.paper.service.PaperService;
import com.phoenix.paper.service.ResearchDirectionService;
import com.phoenix.paper.util.AssertUtil;
import com.phoenix.paper.util.SessionUtils;
import com.phoenix.paper.util.TimeUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.phoenix.paper.common.CommonConstants.PAPER_TYPE;
import static com.phoenix.paper.common.CommonConstants.SEARCH_PAPER_FIELDS;

@Service
public class PaperServiceImpl implements PaperService {

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
    private ResearchDirectionMapper researchDirectionMapper;

    @Autowired
    private PaperQuotationMapper paperQuotationMapper;

    @Autowired
    private PaperDirectionMapper paperDirectionMapper;

    @Autowired
    private ResearchDirectionService researchDirectionService;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private SessionUtils sessionUtils;

    public static void main(String[] args) {
        System.out.println(Arrays.toString(SEARCH_PAPER_FIELDS));
    }

    @Override
    public DetailedPaper getPaperById(Long paperId) {
        Paper paper = paperMapper.selectById(paperId);
        QueryWrapper<PaperDirection> paperDirectionQueryWrapper = new QueryWrapper<>();
        paperDirectionQueryWrapper.eq("paper_id", paper.getId());
        QueryWrapper<PaperQuotation> paperQuotationQueryWrapper = new QueryWrapper<>();
        paperQuotationQueryWrapper.eq("quoter_id", paper.getId());
        SessionData sessionData = sessionUtils.getSessionData();
        return DetailedPaper.builder()
                .paper(paper)
                .paperDirectionList(paperDirectionMapper.selectList(paperDirectionQueryWrapper))
                .paperQuotationList(paperQuotationMapper.selectList(paperQuotationQueryWrapper))
                .canModify(sessionData.getCanModify() == 1 || sessionData.getType() == 1 || sessionData.getId().equals(paper.getUploaderId()))
                .build();
    }

    @Override
    public Page<BriefPaper> getPaperList(int pageNum, int pageSize, int orderBy) {
        if (orderBy == 0) {
            PageHelper.startPage(pageNum, pageSize, "publish_date desc");
        }else{
            PageHelper.startPage(pageNum,pageSize,"like_number+collect_number desc");
        }
        return new Page<>(new PageInfo<>(paperMapper.getPaperList()));

    }

    @Override
    public Long addPaper(Long userId, AddPaperRequest addPaperRequest) throws CommonException {
        SessionData sessionData = sessionUtils.getSessionData();
        AssertUtil.isTrue(sessionData.getCanModify() == 1 || sessionData.getType() == 1, CommonErrorCode.CAN_NOT_MODIFY);
        Paper paper = Paper.builder()
                .uploaderId(userId)
                .paperType(addPaperRequest.getPaperType())
                .author(addPaperRequest.getAuthor())
                .collectNumber(0)
                .likeNumber(0)
                .publishConference(addPaperRequest.getPublishConference())
                .publishDate(TimeUtil.getCurrentTimestamp())
                .link(addPaperRequest.getLink())
                .summary(addPaperRequest.getSummary())
                .build();
        paperMapper.insert(paper);
        User user = userMapper.selectById(userId);
        user.setPaperWeekNum(user.getPaperWeekNum() + 1);
        user.setPaperNum(user.getPaperWeekNum() + 1);
        if (userMapper.updateById(user) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
        for (Long directionId : addPaperRequest.getResearchDirectionList()) {
            paperDirectionMapper.insert(PaperDirection.builder().paperId(paper.getId()).directionId(directionId).createTime(TimeUtil.getCurrentTimestamp()).build());
        }

        IndexRequest request = new IndexRequest("paper");
        request.id(paper.getId().toString());
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");

        request.source(JSON.toJSONString(SearchPaper.builder()
                .id(paper.getId())
                .author(paper.getAuthor())
                .link(paper.getLink())
                .uploader(user.getNickname())
                .publishConference(paper.getPublishConference())
                .title(paper.getTitle())
                .summary(paper.getSummary())
                .publishDate(paper.getPublishDate())
                .paperType(PAPER_TYPE[paper.getPaperType()])), XContentType.JSON);
        try {
            if (!restHighLevelClient.index(request, RequestOptions.DEFAULT).status().toString().equals("CREATED")) {
                throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
            }
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
        }

        return paper.getId();
    }

    @Override
    public  String uploadPaper(MultipartFile file, Long paperId)throws CommonException {
        SessionData sessionData = sessionUtils.getSessionData();
        AssertUtil.isTrue(sessionData.getCanModify() == 1 || sessionData.getType() == 1, CommonErrorCode.CAN_NOT_MODIFY);
        Paper paper = paperMapper.selectById(paperId);
        if (paper == null || paper.getDeleteTime() != null) throw new CommonException(CommonErrorCode.PAPER_NOT_EXIST);
        String originalFilename = file.getOriginalFilename();
        String flag = IdUtil.fastSimpleUUID();
        String rootFilePath = System.getProperty("user.dir") + "/src/main/resources/files/" + flag + "-" + originalFilename;
        try {
            FileUtil.writeBytes(file.getBytes(), rootFilePath);
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.READ_FILE_ERROR);
        }
        String link = CommonConstants.DOWNLOAD_PATH + flag;
        paper.setFileLink(link);
        paper.setUploadTime(TimeUtil.getCurrentTimestamp());
        if (paperMapper.updateById(paper) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
        PDDocument document = null;
        File f = null;
        try {
            InputStream inputStream = file.getInputStream();
            f = new File(Objects.requireNonNull(file.getOriginalFilename()));
            com.phoenix.paper.util.FileUtil.inputStreamToFile(inputStream, f);
            document = PDDocument.load(f);
            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent()) throw new IOException("你没有资格抽取文本");
            int pages = document.getNumberOfPages();
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setStartPage(1);
            stripper.setEndPage(pages);
            String context = stripper.getText(document);
            UpdateRequest updateRequest = new UpdateRequest("paper", paperId.toString());
            updateRequest.timeout("1s");
            updateRequest.doc(JSON.toJSONString(new Tmp(context)), XContentType.JSON);
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            if (!updateResponse.status().toString().equals("OK"))
                throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
            File del = new File(f.toURI());
            del.delete();
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
        }

        return link;
    }

    @Override
    public Page<BriefPaper> findPaperByTitle(Integer pageNum, Integer pageSize, String title) {
        QueryWrapper<Paper> paperQueryWrapper = new QueryWrapper<>();
        paperQueryWrapper.like("title", title);
        paperQueryWrapper.select("title", "id", "publish_time", "summary", "file_link");
        PageHelper.startPage(pageNum, pageSize);
        List<Paper> papers = paperMapper.selectList(paperQueryWrapper);
        List<BriefPaper> briefPaperList = new ArrayList<>();
        for (Paper paper : papers)
            briefPaperList.add(BriefPaper.builder()
                    .fileLink(paper.getFileLink())
                    .id(paper.getId())
                    .title(paper.getTitle())
                    .summary(paper.getSummary())
                    .publishDate(paper.getPublishDate())
                    .build());
        return new Page<>(new PageInfo<>(briefPaperList));
    }

    @Override
    public Long addPaperQuotation(Long quoterId, Long quotedId) {
        SessionData sessionData = sessionUtils.getSessionData();
        AssertUtil.isTrue(sessionData.getCanModify() == 1 || sessionData.getType() == 1, CommonErrorCode.CAN_NOT_MODIFY);
        PaperQuotation paperQuotation = PaperQuotation.builder().quoterId(quoterId).quotedId(quotedId).build();
        paperQuotationMapper.insert(paperQuotation);
        return paperQuotation.getId();
    }

    @Override
    public Page<BriefPaper> searchPaperByDirection(int pageNum, int pageSize, int orderBy, SearchPaperRequest searchPaperRequest) {
        QueryWrapper<Paper> paperQueryWrapper = new QueryWrapper<>();
        if (searchPaperRequest.getTitle() != null) paperQueryWrapper.like("title", searchPaperRequest.getTitle());
        else if (searchPaperRequest.getSummary() != null)
            paperQueryWrapper.like("summary", searchPaperRequest.getSummary());
        else if (searchPaperRequest.getAuthor() != null)
            paperQueryWrapper.like("author", searchPaperRequest.getAuthor());
        if (searchPaperRequest.getResearchDirectionIds().length != 0) {
            HashSet<Long> longHashSet = new HashSet<>();
            for (long id : searchPaperRequest.getResearchDirectionIds()) {
                List<Long> ids = researchDirectionService.getAllSons(id);
                QueryWrapper<PaperDirection> paperDirectionQueryWrapper = new QueryWrapper<>();
                paperDirectionQueryWrapper.isNotNull("delete_time");
                paperDirectionQueryWrapper.and(w -> {
                    for (Long x : ids) {
                        w.or().eq("direction_id", x);
                    }
                    return w;
                });
                List<PaperDirection> paperDirections = paperDirectionMapper.selectList(paperDirectionQueryWrapper);
                for (PaperDirection paperDirection : paperDirections) longHashSet.add(paperDirection.getPaperId());
            }
            paperQueryWrapper.in("id", longHashSet);
        }
        paperQueryWrapper.select("id", "title", "publish_date", "summary", "author", "file_link");
        if (orderBy == 0) {
            PageHelper.startPage(pageNum, pageSize, "paper_date desc");
        } else {
            PageHelper.startPage(pageNum, pageSize, "like_number+collect_number desc");
        }
        List<Paper> papers = paperMapper.selectList(paperQueryWrapper);
        List<BriefPaper> briefPaperList = new ArrayList<>();
        for (Paper paper : papers)
            briefPaperList.add(BriefPaper.builder()
                    .fileLink(paper.getFileLink())
                    .id(paper.getId())
                    .title(paper.getTitle())
                    .summary(paper.getSummary())
                    .publishDate(paper.getPublishDate())
                    .build());
        return new Page<>(new PageInfo<>(briefPaperList));
    }

    @Override
    public void deletePaper(Long paperId, Long userId) throws CommonException {
        Paper paper = paperMapper.selectById(paperId);
        User user = userMapper.selectById(userId);
        if (!paper.getUploaderId().equals(userId) && user.getType() != 1 && user.getCanModify() != 1)
            throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
        String deleteTime = TimeUtil.getCurrentTimestamp();
        QueryWrapper<Note> noteQueryWrapper = new QueryWrapper<>();
        noteQueryWrapper.eq("paper_id", paper.getId());
        List<Note> notes = noteMapper.selectList(noteQueryWrapper);
        for (Note note : notes) {
            note.setDeleteTime(deleteTime);
            if (noteMapper.updateById(note) == 0) throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
            QueryWrapper<Likes> likesQueryWrapper = new QueryWrapper<>();
            likesQueryWrapper.eq("object_id", note.getId()).eq("object_type", 1);
            if (likesMapper.update(Likes.builder().deleteTime(deleteTime).build(), likesQueryWrapper) == 0)
                throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
            QueryWrapper<Collection> collectionQueryWrapper = new QueryWrapper<>();
            collectionQueryWrapper.eq("object_id", note.getId()).eq("object_type", 1);
            if (collectionMapper.update(Collection.builder().deleteTime(deleteTime).build(), collectionQueryWrapper) == 0)
                throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.eq("object_id", note.getId()).eq("object_type", 0);
            List<Comment> comments = commentMapper.selectList(commentQueryWrapper);
            for (Comment comment : comments) {
                QueryWrapper<Comment> commentQueryWrapper1 = new QueryWrapper<>();
                commentQueryWrapper1.eq("object_id", comment.getId()).eq("object_type", 1);
                if (commentMapper.update(Comment.builder().deleteTime(deleteTime).build(), commentQueryWrapper1) == 0)
                    throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
            }
            if (commentMapper.update(Comment.builder().deleteTime(deleteTime).build(), commentQueryWrapper) == 0)
                throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);

        }
        QueryWrapper<Likes> likesQueryWrapper = new QueryWrapper<>();
        likesQueryWrapper.eq("object_id", paper.getId()).eq("object_type", 0);
        if (likesMapper.update(Likes.builder().deleteTime(deleteTime).build(), likesQueryWrapper) == 0)
            throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
        QueryWrapper<Collection> collectionQueryWrapper = new QueryWrapper<>();
        collectionQueryWrapper.eq("object_id", paper.getId()).eq("object_type", 0);
        if (collectionMapper.update(Collection.builder().deleteTime(deleteTime).build(), collectionQueryWrapper) == 0)
            throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
        QueryWrapper<PaperQuotation> paperQuotationQueryWrapper = new QueryWrapper<>();
        paperQuotationQueryWrapper.eq("paper_id", paper.getId());
        if (paperQuotationMapper.update(PaperQuotation.builder().deleteTime(deleteTime).build(), paperQuotationQueryWrapper) == 0)
            throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
        QueryWrapper<PaperDirection> paperDirectionQueryWrapper = new QueryWrapper<>();
        if (paperDirectionMapper.update(PaperDirection.builder().deleteTime(deleteTime).build(), paperDirectionQueryWrapper) == 0)
            throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
        DeleteRequest deleteRequest = new DeleteRequest("paper", paperId.toString());
        deleteRequest.timeout("1s");
        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            if (!deleteResponse.status().toString().equals("OK"))
                throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
        }

    }

    @Override
    public void updatePaper(Long paperId, Long userId, UpdatePaperRequest updatePaperRequest) throws CommonException {
        SessionData sessionData = sessionUtils.getSessionData();
        Paper paper = paperMapper.selectById(paperId);
        AssertUtil.isTrue(sessionData.getCanModify() == 1 || sessionData.getType() == 1 || sessionData.getId().equals(paper.getUploaderId()), CommonErrorCode.CAN_NOT_MODIFY);
        paperMapper.updateById(
                Paper.builder().paperType(updatePaperRequest.getPaperType())
                        .link(updatePaperRequest.getLink())
                        .summary(updatePaperRequest.getSummary())
                        .publishConference(updatePaperRequest.getPublishConference())
                        .id(paperId)
                        .publishDate(updatePaperRequest.getPublishDate())
                        .author(updatePaperRequest.getAuthor())
                        .title(updatePaperRequest.getTitle())
                        .build());

        UpdateRequest updateRequest = new UpdateRequest("paper", paperId.toString());
        updateRequest.timeout("1s");
        updateRequest.doc(JSON.toJSONString(updateRequest), XContentType.JSON);
        try {
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            if (!updateResponse.status().toString().equals("OK"))
                throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
        }
    }

    @Override
    public List<Map<String, Object>> searchPaper(String contents, int pageNum, int pageSize) {
        SearchRequest searchRequest = new SearchRequest("paper");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from((pageNum - 1) * pageSize);
        sourceBuilder.size(pageSize);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        MultiMatchQueryBuilder multiMatchQueryBuilder = new MultiMatchQueryBuilder(contents, SEARCH_PAPER_FIELDS[0], SEARCH_PAPER_FIELDS[1], SEARCH_PAPER_FIELDS[2], SEARCH_PAPER_FIELDS[3], SEARCH_PAPER_FIELDS[4], SEARCH_PAPER_FIELDS[5], SEARCH_PAPER_FIELDS[6], SEARCH_PAPER_FIELDS[7], SEARCH_PAPER_FIELDS[8]);

        sourceBuilder.query(multiMatchQueryBuilder);
        sourceBuilder.highlighter();

        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            ArrayList<Map<String, Object>> page = new ArrayList<>();
            for (SearchHit documentFields : searchResponse.getHits().getHits()) {
                page.add(documentFields.getSourceAsMap());
            }
            return page;
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
        }
    }

    @Override
    public Long addQuotation(Long quoterId, Long quotedId, String remarks) {
        PaperQuotation paperQuotation = PaperQuotation.builder()
                .quoterId(quoterId)
                .quotedId(quotedId)
                .remarks(remarks)
                .build();
        paperQuotationMapper.insert(paperQuotation);
        return paperQuotation.getId();
    }

    @Override
    public void updateQuotation(Long id, String remarks) {
        PaperQuotation paperQuotation = paperQuotationMapper.selectById(id);
        paperQuotation.setRemarks(remarks);
        paperQuotationMapper.updateById(paperQuotation);
    }

    @Override
    public void deleteQuotation(Long id) {
        PaperQuotation paperQuotation = paperQuotationMapper.selectById(id);
        paperQuotation.setDeleteTime(TimeUtil.getCurrentTimestamp());
        paperQuotationMapper.updateById(paperQuotation);
    }

    @Override
    public List<Paper> searchPaperBefore(Long quoterId, String title) throws CommonException {
        Paper paper = paperMapper.selectById(quoterId);
        AssertUtil.isTrue(paper != null, CommonErrorCode.PAPER_NOT_EXIST);
        QueryWrapper<Paper> paperQueryWrapper = new QueryWrapper<>();
        if (title.length() == 0)
            paperQueryWrapper.select("id", "title").lt("publish_date", paper.getPublishDate()).orderByAsc("publish_date");
        else
            paperQueryWrapper.select("id", "title").lt("publish_date", paper.getPublishDate()).like("title", "%" + title + "%").orderByAsc("publish_date");
        return paperMapper.selectList(paperQueryWrapper);
    }

    @Override
    public Long addDirection(Long paperId, Long directionId) {
        PaperDirection paperDirection = PaperDirection.builder()
                .directionId(directionId)
                .paperId(paperId)
                .build();
        paperDirectionMapper.insert(paperDirection);
        return paperDirection.getId();
    }

    @Override
    public void deleteDirection(Long paperDirectionId) {
        PaperDirection paperDirection = paperDirectionMapper.selectById(paperDirectionId);
        paperDirection.setDeleteTime(TimeUtil.getCurrentTimestamp());
        paperDirectionMapper.updateById(paperDirection);
    }

    private static class Tmp {
        public String context;

        Tmp(String context) {
            this.context = context;
        }
    }
}

