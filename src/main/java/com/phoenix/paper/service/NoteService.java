package com.phoenix.paper.service;

import com.phoenix.paper.common.Page;
import com.phoenix.paper.controller.request.AddNoteRequest;
import com.phoenix.paper.controller.request.SearchNoteRequest;
import com.phoenix.paper.controller.request.UpdateNoteRequest;
import com.phoenix.paper.dto.BriefNote;
import com.phoenix.paper.entity.Note;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


public interface NoteService {
    String uploadCover(MultipartFile file, Long noteId);

    Long addNote(AddNoteRequest addNoteRequest);

    Page<Note> getNoteList(int pageSize, int pageNumber, int sortRule);

    Page<BriefNote> searchNoteByBody(SearchNoteRequest searchNoteRequest);

    List<Map<String, Object>> searchNoteByQuery(String contents, int pageNum, int pageSize);

    Note getNoteDetails(Long noteId);

    void deleteNote(Long noteId);

    void updateNote(Long noteId, UpdateNoteRequest updateNoteRequest);
}
