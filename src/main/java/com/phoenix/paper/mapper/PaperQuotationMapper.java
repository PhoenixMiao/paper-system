package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.entity.PaperQuotation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface PaperQuotationMapper extends BaseMapper<PaperQuotation> {
    @Update("UPDATE paper_quotation SET delete_time = #{delete_time} WHERE quoter_id = #{paper_id} OR quoted_id = #{paper_id};")
    void deletePaper(@Param("delete_time") String deleteTime, @Param("paper_id") Long paperId);

    @Select("SELECT COUNT(*) FROM paper_quotation WHERE quoted_id = #{paperId} AND deleteTime IS NULL;")
    int getQuotedNumber(@Param("paperId") Long paperId);
}
