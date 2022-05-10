package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.entity.Note;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface NoteMapper extends BaseMapper<Note> {

    @Select("SELECT like_number FROM note where id=#{id}")
    Long getNoteLikes(@Param("id")Long id);

    @Select("SELECT collect_number FROM note where id=#{id}")
    Long getNoteCollects(@Param("id")Long id);


    @Select("SELECT * FROM note WHERE delete_time IS NULL")
    List<Note> getNoteList();
}
