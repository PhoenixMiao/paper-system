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
import com.phoenix.paper.service.*;
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
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.phoenix.paper.service.LikeService.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.phoenix.paper.common.CommonConstants.*;

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
    private LikeService likeService;

    @Autowired
    private CollectionService collectionService;

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
        System.out.println(new StringBuilder("12345").toString());
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

    @Transactional(rollbackFor = CommonException.class)
    @Override
    public Long addPaper(AddPaperRequest addPaperRequest) throws CommonException {
        SessionData sessionData = sessionUtils.getSessionData();
        Long userId = sessionData.getId();
        Paper paper = Paper.builder()
                .title(addPaperRequest.getTitle())
                .paperType(addPaperRequest.getPaperType())
                .uploaderId(userId)
                .paperType(addPaperRequest.getPaperType())
                .author(addPaperRequest.getAuthor())
                .collectNumber(0)
                .likeNumber(0)
                .version(1)
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
                .publishDate(TimeUtil.parseToDate(paper.getPublishDate()))
                .paperType(PAPER_TYPE[paper.getPaperType()]).build()), XContentType.JSON);
        try {
            if (!restHighLevelClient.index(request, RequestOptions.DEFAULT).status().toString().equals("CREATED")) {
                throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
            }
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
        }

        return paper.getId();
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    public  String uploadPaper(MultipartFile file, Long paperId)throws CommonException {
        SessionData sessionData = sessionUtils.getSessionData();
        Paper paper = paperMapper.selectById(paperId);
        AssertUtil.isTrue(sessionData.getCanModify() == 1 || sessionData.getType() == 1 || paper.getUploaderId().equals(sessionData.getId()), CommonErrorCode.CAN_NOT_MODIFY);
        if (paper == null || paper.getDeleteTime() != null) throw new CommonException(CommonErrorCode.PAPER_NOT_EXIST);
        String originalFilename = file.getOriginalFilename();
        String flag = IdUtil.fastSimpleUUID();
        String rootFilePath = PAPER_FILE_PATH + flag + "-" + originalFilename;
        try {
            FileUtil.writeBytes(file.getBytes(), rootFilePath);
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.READ_FILE_ERROR);
        }
        String link = CommonConstants.DOWNLOAD_PAPER_PATH + flag;
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
            //todo 这里直接在toJsonString里面直接new Tmp就不行
            Tmp tmp = new Tmp(context);
            updateRequest.doc(JSON.toJSONString(tmp), XContentType.JSON);
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
        if (searchPaperRequest.getResearchDirectionIds()!=null && searchPaperRequest.getResearchDirectionIds().length != 0) {
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

    @Transactional(rollbackFor = CommonException.class)
    @Override
    public void deletePaper(Long paperId) throws CommonException {
        Paper paper = paperMapper.selectById(paperId);
        SessionData user = sessionUtils.getSessionData();
        Long userId = user.getId();
        if (!paper.getUploaderId().equals(userId) && user.getType() != 1 && user.getCanModify() != 1)
            throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
        String deleteTime = TimeUtil.getCurrentTimestamp();
        QueryWrapper<Note> noteQueryWrapper = new QueryWrapper<>();
        noteQueryWrapper.eq("paper_id", paper.getId());
        List<Note> notes = noteMapper.selectList(noteQueryWrapper);
        for (Note note : notes) {
            note.setDeleteTime(deleteTime);
            noteMapper.updateById(note);
            QueryWrapper<Likes> likesQueryWrapper = new QueryWrapper<>();
            likesQueryWrapper.eq("object_id", note.getId()).eq("object_type", 1);
            likesMapper.update(Likes.builder().deleteTime(deleteTime).build(), likesQueryWrapper);
            QueryWrapper<Collection> collectionQueryWrapper = new QueryWrapper<>();
            collectionQueryWrapper.eq("object_id", note.getId()).eq("object_type", 1);
            collectionMapper.update(Collection.builder().deleteTime(deleteTime).build(), collectionQueryWrapper);
            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.eq("object_id", note.getId()).eq("object_type", 0);
            List<Comment> comments = commentMapper.selectList(commentQueryWrapper);
            for (Comment comment : comments) {
                QueryWrapper<Comment> commentQueryWrapper1 = new QueryWrapper<>();
                commentQueryWrapper1.eq("object_id", comment.getId()).eq("object_type", 1);
                commentMapper.update(Comment.builder().deleteTime(deleteTime).build(), commentQueryWrapper1);
            }
            commentMapper.update(Comment.builder().deleteTime(deleteTime).build(), commentQueryWrapper);
        }
        QueryWrapper<Likes> likesQueryWrapper = new QueryWrapper<>();
        likesQueryWrapper.eq("object_id", paper.getId()).eq("object_type", 0);
        likesMapper.update(Likes.builder().deleteTime(deleteTime).build(), likesQueryWrapper);
        QueryWrapper<Collection> collectionQueryWrapper = new QueryWrapper<>();
        collectionQueryWrapper.eq("object_id", paper.getId()).eq("object_type", 0);
        collectionMapper.update(Collection.builder().deleteTime(deleteTime).build(), collectionQueryWrapper);
        QueryWrapper<PaperQuotation> paperQuotationQueryWrapper = new QueryWrapper<>();
        paperQuotationQueryWrapper.eq("quoter_id", paper.getId()).or().eq("quoted_id", paper.getId());
        paperQuotationMapper.update(PaperQuotation.builder().deleteTime(deleteTime).build(), paperQuotationQueryWrapper);
        QueryWrapper<PaperDirection> paperDirectionQueryWrapper = new QueryWrapper<>();
        paperDirectionMapper.update(PaperDirection.builder().deleteTime(deleteTime).build(), paperDirectionQueryWrapper);
        paper.setDeleteTime(TimeUtil.getCurrentTimestamp());
        if (paperMapper.updateById(paper) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
        DeleteRequest deleteRequest = new DeleteRequest("paper", paperId.toString());
        deleteRequest.timeout("1s");
        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            if (!deleteResponse.status().toString().equals("OK"))
                throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
        }
        if (paper.getFileLink() != null) {
            List<String> fileNames = FileUtil.listFileNames(PAPER_FILE_PATH);
            String fileName = fileNames.stream().filter(name -> name.contains(paper.getFileLink().substring(43))).findAny().orElse("");
            if (!fileName.equals("")) FileUtil.del(PAPER_FILE_PATH + fileName);
        }
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    public void updatePaper(Long paperId, UpdatePaperRequest updatePaperRequest) throws CommonException {
        SessionData sessionData = sessionUtils.getSessionData();
        Paper paper = paperMapper.selectById(paperId);
        AssertUtil.isTrue(sessionData.getCanModify() == 1 || sessionData.getType() == 1 || sessionData.getId().equals(paper.getUploaderId()), CommonErrorCode.CAN_NOT_MODIFY);
        paper.setPaperType(updatePaperRequest.getPaperType());
        paper.setAuthor(updatePaperRequest.getAuthor());
        paper.setLink(updatePaperRequest.getLink());
        paper.setPublishConference(updatePaperRequest.getPublishConference());
        paper.setPublishDate(updatePaperRequest.getPublishDate());
        paper.setSummary(updatePaperRequest.getSummary());
        paper.setTitle(updatePaperRequest.getTitle());
        if (paperMapper.updateById(paper) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);

        UpdateRequest updateRequest = new UpdateRequest("paper", paperId.toString());
        updateRequest.timeout("1s");
        updateRequest.doc(JSON.toJSONString(updatePaperRequest), XContentType.JSON);
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

        MultiMatchQueryBuilder multiMatchQueryBuilder = new MultiMatchQueryBuilder(contents, SEARCH_PAPER_FIELDS[0], SEARCH_PAPER_FIELDS[1], SEARCH_PAPER_FIELDS[2], SEARCH_PAPER_FIELDS[3], SEARCH_PAPER_FIELDS[4], SEARCH_PAPER_FIELDS[5], SEARCH_PAPER_FIELDS[6], SEARCH_PAPER_FIELDS[7]);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.requireFieldMatch();
        highlightBuilder.field(SEARCH_PAPER_FIELDS[0]).field(SEARCH_PAPER_FIELDS[2]).field(SEARCH_PAPER_FIELDS[3]).field(SEARCH_PAPER_FIELDS[4]).field(SEARCH_PAPER_FIELDS[5]).field(SEARCH_PAPER_FIELDS[6]);
        highlightBuilder.preTags("<span style='color:orange'>");
        highlightBuilder.postTags("</span>");


        sourceBuilder.query(multiMatchQueryBuilder);
        sourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            ArrayList<Map<String, Object>> page = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                Map<String, HighlightField> map = hit.getHighlightFields();
                Map<String, Object> resultMap = hit.getSourceAsMap();

                HighlightField title = map.get("title");
                if (title != null) {
                    Text[] fragments = title.fragments();
                    StringBuilder newTitle = new StringBuilder();
                    for (Text text : fragments) newTitle.append(text);
                    resultMap.put("title", newTitle.toString());
                }

                HighlightField author = map.get("author");
                if (author != null) {
                    Text[] fragments = author.fragments();
                    StringBuilder newAuthor = new StringBuilder();
                    for (Text text : fragments) newAuthor.append(text);
                    resultMap.put("author", newAuthor.toString());
                }

                HighlightField publishConference = map.get("publishConference");
                if (publishConference != null) {
                    Text[] fragments = publishConference.fragments();
                    StringBuilder newpublishConference = new StringBuilder();
                    for (Text text : fragments) newpublishConference.append(text);
                    resultMap.put("publishConference", newpublishConference.toString());
                }

                HighlightField summary = map.get("summary");
                if (summary != null) {
                    Text[] fragments = summary.fragments();
                    StringBuilder newsummary = new StringBuilder();
                    for (Text text : fragments) newsummary.append(text);
                    resultMap.put("summary", newsummary.toString());
                }

                HighlightField paperType = map.get("paperType");
                if (paperType != null) {
                    Text[] fragments = paperType.fragments();
                    StringBuilder newpaperType = new StringBuilder();
                    for (Text text : fragments) newpaperType.append(text);
                    resultMap.put("paperType", newpaperType.toString());
                }

                HighlightField uploader = map.get("uploader");
                if (uploader != null) {
                    Text[] fragments = uploader.fragments();
                    StringBuilder newuploader = new StringBuilder();
                    for (Text text : fragments) newuploader.append(text);
                    resultMap.put("uploader", newuploader.toString());
                }

                resultMap.put("context", "");

                Long paperId = Long.valueOf(hit.getSourceAsMap().get("id").toString());

                resultMap.put("likeNum", likeService.getLikeNumber(paperId,0));

                resultMap.put("collectNum", collectionService.getCollectNumber(paperId,0));

                page.add(resultMap);

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

    @Transactional(rollbackFor = CommonException.class)
    @Override
    public void updateQuotation(Long id, String remarks) throws CommonException {
        PaperQuotation paperQuotation = paperQuotationMapper.selectById(id);
        paperQuotation.setRemarks(remarks);
        if (paperQuotationMapper.updateById(paperQuotation) == 0)
            throw new CommonException(CommonErrorCode.UPDATE_FAILED);
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    public void deleteQuotation(Long id) throws CommonException {
        PaperQuotation paperQuotation = paperQuotationMapper.selectById(id);
        paperQuotation.setDeleteTime(TimeUtil.getCurrentTimestamp());
        if (paperQuotationMapper.updateById(paperQuotation) == 0)
            throw new CommonException(CommonErrorCode.UPDATE_FAILED);
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

    @Transactional(rollbackFor = CommonException.class)
    @Override
    public void deleteDirection(Long paperDirectionId) throws CommonException {
        PaperDirection paperDirection = paperDirectionMapper.selectById(paperDirectionId);
        paperDirection.setDeleteTime(TimeUtil.getCurrentTimestamp());
        if (paperDirectionMapper.updateById(paperDirection) == 0) throw new CommonException();
    }

    private static class Tmp {
        public String context;

        Tmp(String context) {
            this.context = context;
        }
    }


}

