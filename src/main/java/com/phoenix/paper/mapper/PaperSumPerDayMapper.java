package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.dto.PaperAndNoteData;
import com.phoenix.paper.entity.NoteSumPerDay;
import com.phoenix.paper.entity.Paper;
import com.phoenix.paper.entity.PaperSumPerDay;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PaperSumPerDayMapper  extends BaseMapper<PaperSumPerDay> {

    @Select("SELECT direction,number FROM paper_sum_per_day WHERE user_id=#{user_id}")
    List<PaperAndNoteData> getPaperData(@Param("user_id")Long userId);
}
