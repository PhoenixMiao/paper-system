package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.dto.BriefUser;
import com.phoenix.paper.dto.PaperAndNoteData;
import com.phoenix.paper.entity.NoteSumPerDay;
import com.phoenix.paper.entity.PaperDirection;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface NoteSumPerDayMapper extends BaseMapper<NoteSumPerDay> {

    @Select("SELECT direction, CASE #{period} WHEN 7 THEN number_week WHEN 30 THEN number_month ELSE number_year END AS number FROM note_sum_per_day WHERE user_id=#{user_id}")
    List<PaperAndNoteData> getNoteData(@Param("user_id")Long userId,@Param("period")Integer period);
}
