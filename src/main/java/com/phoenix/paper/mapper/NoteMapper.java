package com.phoenix.paper.mapper;

import com.phoenix.paper.MyMapper;
import com.phoenix.paper.entity.Note;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface NoteMapper extends MyMapper<Note> {

    @Select("SELECT like_number FROM note where id=#{id}")
    Long getNoteLikes(@Param("id")Long id);

    @Update("UPDATE note set like_number=#{likeNumber} where id=#{id}")
    void setNoteLikes(@Param("id")Long id,@Param("likeNumber")Long likeNumber);

    @Select("SELECT collect_number FROM note where id=#{id}")
    Long getNoteCollects(@Param("id")Long id);

    @Update("UPDATE note set collect_number=#{collectNumber} where id=#{id}")
    void setNoteCollects(@Param("id")Long id,@Param("collectNumber")Long collectNumber);

    @Select("SELECT * FROM note WHERE delete_time IS NULL")
    List<Note> getNoteList();

    @Update("UPDATE note SET delete_time = #{delete_time} WHERE author_id = #{author_id};")
    void deleteNoteByAuthorId(@Param("delete_time")String deleteTime,@Param("author_id")Long author_id);

    @Update("UPDATE note SET delete_time = #{delete_time} WHERE paper_id = #{paper_id};")
    void deleteNoteByPaperId(@Param("delete_time")String deleteTime,@Param("paper_id")Long paperId);
}
