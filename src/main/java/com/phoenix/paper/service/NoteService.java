package com.phoenix.paper.service;

import com.phoenix.paper.common.Page;
import com.phoenix.paper.entity.Note;
import org.springframework.web.multipart.MultipartFile;


public interface NoteService {
    String uploadNote(MultipartFile file,Long noteId);

    Long addNote(Long authorId,Long paperId);

    Page<Note> getNoteList(int pageSize, int pageNumber, int sortRule);
}
