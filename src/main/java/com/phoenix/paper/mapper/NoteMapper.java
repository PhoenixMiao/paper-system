package com.phoenix.paper.mapper;

import com.phoenix.paper.MyMapper;
import com.phoenix.paper.entity.Note;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface NoteMapper extends MyMapper<Note> {

    @Select("SELECT like_number FROM note where id=#{id}")
    Long getNoteLikes(@Param("id")Long id);

    @Update("UPDATE note set like_number=#{likeNumber} where id=#{id}")
    void setNoteLikes(@Param("id")Long id,@Param("likeNumber")Long likeNumber);

    @Select("SELECT collect_number FROM note where id=#{id}")
    Long getNoteCollects(@Param("id")Long id);

    @Update("UPDATE note set collect_number=#{collectNumber} where id=#{id}")
    void setNoteCollects(@Param("id")Long id,@Param("collectNumber")Long collectNumber);
}
