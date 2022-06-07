package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.dto.PaperAndNoteData;
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

    @Select("SELECT direction_id as direction,count(*) as number FROM note LEFT JOIN paper_direction ON note.paper_id=paper_direction.paper_id  where author_id=#{userId} AND TO_DAYS(NOW())-TO_DAYS(upload_time)<=#{period} group by direction_id ")
    List<PaperAndNoteData> getNoteData(@Param("user_id")Long userId, @Param("period")Integer period);
}
