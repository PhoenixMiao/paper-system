package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.dto.BriefNote;
import com.phoenix.paper.dto.PaperAndNoteData;
import com.phoenix.paper.entity.Note;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface NoteMapper extends BaseMapper<Note> {

    @Select("SELECT like_number FROM note where id=#{id}")
    Long getNoteLikes(@Param("id")Long id);

    @Select("SELECT collect_number FROM note where id=#{id}")
    Long getNoteCollects(@Param("id") Long id);

    @Select("SELECT * FROM note WHERE delete_time IS NULL")
    List<Note> getNoteList();

    @Select("SELECT name as direction,count(*) as number FROM (note LEFT JOIN paper_direction ON note.paper_id=paper_direction.paper_id )LEFT JOIN research_direction ON paper_direction.direction_id=research_direction.id where author_id=#{user_id} AND TO_DAYS(NOW())-TO_DAYS(note.create_time)<=#{period} AND note.delete_time IS NULL group by direction_id")
    List<PaperAndNoteData> getNoteData(@Param("user_id") Long userId, @Param("period") Integer period);

    @Select("SELECT id,author_id,author,title,cover,create_time,like_number,collect_number FROM note LEFT JOIN user ON note.author_id = user.id WHERE user.nickname LIKE %#{author}%;")
    List<BriefNote> getBriefNoteListByAuthor(@Param("author") String author);

    @Select("SELECT id,author_id,author,title,cover,create_time,like_number,collect_number FROM note WHERE title LIKE #{title}%;")
    List<BriefNote> getBriefNoteListByTitle(@Param("author") String title);

    @Select("SELECT id,author_id,author,title,cover,create_time,like_number,collect_number FROM note;")
    List<BriefNote> getBriefNoteList();

    @Select("SELECT id,author_id,author,title,cover,create_time,like_number,collect_number FROM note WHERE author_id=#{userId} AND delete_time IS NULL;")
    List<BriefNote> getUserNoteList(@Param("userId") Long userId);
}
