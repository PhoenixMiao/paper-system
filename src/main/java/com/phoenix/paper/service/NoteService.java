package com.phoenix.paper.service;

import org.springframework.web.multipart.MultipartFile;

public interface NoteService {
    String uploadNote(MultipartFile file,Long noteId);

    Long addNote(Long authorId,Long paperId);
}
