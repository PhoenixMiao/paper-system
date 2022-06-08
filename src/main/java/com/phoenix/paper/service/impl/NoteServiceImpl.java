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
import com.phoenix.paper.controller.request.AddNoteRequest;
import com.phoenix.paper.controller.request.SearchNoteRequest;
import com.phoenix.paper.controller.request.UpdateNoteRequest;
import com.phoenix.paper.dto.BriefNote;
import com.phoenix.paper.dto.SearchNote;
import com.phoenix.paper.dto.SessionData;
import com.phoenix.paper.entity.*;
import com.phoenix.paper.mapper.*;
import com.phoenix.paper.service.NoteService;
import com.phoenix.paper.util.AssertUtil;
import com.phoenix.paper.util.SessionUtils;
import com.phoenix.paper.util.TimeUtil;
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
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.phoenix.paper.common.CommonConstants.NOTE_FILE_PATH;
import static com.phoenix.paper.common.CommonConstants.SEARCH_NOTE_FIELDS;

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

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //todo 事务，高亮，权值搜索，不展现context

    @Transactional
    @Override
    public String uploadCover(MultipartFile file, Long noteId) throws CommonException {
        SessionData sessionData = sessionUtils.getSessionData();
        AssertUtil.isTrue(sessionData.getCanModify() == 1 || sessionData.getType() == 1, CommonErrorCode.CAN_NOT_MODIFY);
        Note note = noteMapper.selectById(noteId);
        if (note == null || note.getDeleteTime() != null) throw new CommonException(CommonErrorCode.NOTE_NOT_EXIST);
        String originalFilename = file.getOriginalFilename();
        String flag = IdUtil.fastSimpleUUID();
        String rootFilePath = NOTE_FILE_PATH + flag + "-" + originalFilename;
        try {
            FileUtil.writeBytes(file.getBytes(), rootFilePath);
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.READ_FILE_ERROR);
        }
        String link = CommonConstants.DOWNLOAD_NOTE_PATH + flag;
        note.setCover(link);
        if(noteMapper.updateById(note)==0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);
        return link;
    }

    @Transactional
    @Override
    public Long addNote(AddNoteRequest addNoteRequest) throws CommonException {
        SessionData sessionData = sessionUtils.getSessionData();
        Long authorId = sessionData.getId();
        AssertUtil.isTrue(sessionData.getCanModify() == 1 || sessionData.getType() == 1, CommonErrorCode.CAN_NOT_MODIFY);
        Paper paper = paperMapper.selectById(addNoteRequest.getPaperId());
        if (paper == null || paper.getDeleteTime() != null) throw new CommonException(CommonErrorCode.PAPER_NOT_EXIST);
        Note note = Note.builder()
                .authorId(authorId)
                .createTime(TimeUtil.getCurrentTimestamp())
                .paperId(addNoteRequest.getPaperId())
                .html(addNoteRequest.getHtml())
                .title(addNoteRequest.getTitle())
                .version(1)
                .build();
        noteMapper.insert(note);
        User user = userMapper.selectById(authorId);
        user.setNoteNum(user.getNoteNum() + 1);
        user.setNoteWeekNum(user.getNoteWeekNum() + 1);
        if (userMapper.updateById(user) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);

        IndexRequest request = new IndexRequest("note");
        request.id(note.getId().toString());
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");

        request.source(JSON.toJSONString(SearchNote.builder()
                .id(note.getId())
                .author(user.getNickname())
                .context(addNoteRequest.getText())
                .title(addNoteRequest.getTitle()).build()), XContentType.JSON);
        try {
            if (!restHighLevelClient.index(request, RequestOptions.DEFAULT).status().toString().equals("CREATED")) {
                throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
            }
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
        }

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
    public Page<BriefNote> searchNoteByBody(SearchNoteRequest searchNoteRequest) throws CommonException {
        QueryWrapper<Note> noteQueryWrapper = new QueryWrapper<>();
        if (searchNoteRequest.getTitle() != null)
            noteQueryWrapper.like("title", searchNoteRequest.getTitle());
        else if (searchNoteRequest.getAuthor() != null)
            noteQueryWrapper.like("author", searchNoteRequest.getAuthor());
        noteQueryWrapper.select("id", "author_id", "title", "cover", "create_time", "like_number", "collect_number");
        if (searchNoteRequest.getOrderby() == 0) {
            PageHelper.startPage(searchNoteRequest.getPageNum(), searchNoteRequest.getPageSize(), "create_time desc");
        } else {
            PageHelper.startPage(searchNoteRequest.getPageNum(), searchNoteRequest.getPageSize(), "like_number+collect_number desc");
        }
        List<Note> notes = noteMapper.selectList(noteQueryWrapper);
        List<BriefNote> briefNoteList = new ArrayList<>();
        for (Note note : notes)
            briefNoteList.add(BriefNote.builder()
                    .id(note.getId())
                    .authorId(note.getAuthorId())
                    .collectNumber(note.getCollectNumber())
                    .cover(note.getCover())
                    .title(note.getTitle())
                    .createTime(note.getCreateTime())
                    .likeNumber(note.getLikeNumber())
                    .build());
        return new Page<>(new PageInfo<>(briefNoteList));
    }

    @Override
    public Note getNoteDetails(Long noteId) throws CommonException{
        Note note=noteMapper.selectById(noteId);
        if(note==null || note.getDeleteTime()!=null)throw new CommonException(CommonErrorCode.NOTE_NOT_EXIST);
        return note;
    }

    @Transactional
    @Override
    public void deleteNote(Long noteId) throws CommonException {
        SessionData user = sessionUtils.getSessionData();
        Note note = noteMapper.selectById(noteId);
        Long userId = user.getId();
        if (!note.getAuthorId().equals(userId) && user.getType() != 1 && user.getCanModify() != 1)
            throw new CommonException(CommonErrorCode.CAN_NOT_DELETE);
        String deleteTime = TimeUtil.getCurrentTimestamp();
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
        DeleteRequest deleteRequest = new DeleteRequest("note", noteId.toString());
        deleteRequest.timeout("1s");
        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            if (!deleteResponse.status().toString().equals("OK"))
                throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
        }
        if (note.getCover() != null) {
            List<String> fileNames = FileUtil.listFileNames(NOTE_FILE_PATH);
            String fileName = fileNames.stream().filter(name -> name.contains(note.getCover().substring(43))).findAny().orElse("");
            if (!fileName.equals("")) FileUtil.del(NOTE_FILE_PATH + fileName);
        }
    }

    @Override
    public List<Map<String, Object>> searchNoteByQuery(String contents, int pageNum, int pageSize) throws CommonException {
        SearchRequest searchRequest = new SearchRequest("note");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from((pageNum - 1) * pageSize);
        sourceBuilder.size(pageSize);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        MultiMatchQueryBuilder multiMatchQueryBuilder = new MultiMatchQueryBuilder(contents, SEARCH_NOTE_FIELDS[0], SEARCH_NOTE_FIELDS[1], SEARCH_NOTE_FIELDS[2]);
        multiMatchQueryBuilder.type(MultiMatchQueryBuilder.Type.BEST_FIELDS);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.requireFieldMatch();
        highlightBuilder.field(SEARCH_NOTE_FIELDS[0]).field(SEARCH_NOTE_FIELDS[1]).field(SEARCH_NOTE_FIELDS[2]);
        highlightBuilder.preTags("<span style='color:orange'>");
        highlightBuilder.postTags("</span>");

        sourceBuilder.query(multiMatchQueryBuilder);
        sourceBuilder.highlighter(highlightBuilder);

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
    public void updateNote(Long noteId, UpdateNoteRequest updateNoteRequest) throws CommonException {
        Note note = noteMapper.selectById(noteId);
        AssertUtil.isTrue(sessionUtils.getSessionData().getType() == 1 || sessionUtils.getSessionData().getCanModify() == 1 ||
                note.getAuthorId().equals(sessionUtils.getUserId()), CommonErrorCode.CAN_NOT_MODIFY);
        if (updateNoteRequest.getHtml() != null) note.setHtml(updateNoteRequest.getHtml());
        if (updateNoteRequest.getTitle() != null) note.setTitle(updateNoteRequest.getTitle());
        if (noteMapper.updateById(note) == 0) throw new CommonException(CommonErrorCode.UPDATE_FAILED);

        UpdateRequest updateRequest = new UpdateRequest("note", noteId.toString());
        updateRequest.timeout("1s");
        SearchNote searchNote = new SearchNote();
        if (updateNoteRequest.getText() != null) searchNote.setContext(updateNoteRequest.getText());
        if (updateNoteRequest.getTitle() != null) searchNote.setTitle(updateNoteRequest.getTitle());
        updateRequest.doc(JSON.toJSONString(searchNote), XContentType.JSON);
        try {
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            if (!updateResponse.status().toString().equals("OK"))
                throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.DOC_INDEX_FAILED);
        }
    }

}
