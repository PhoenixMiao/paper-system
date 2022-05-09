package com.phoenix.paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phoenix.paper.entity.PaperQuotation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface PaperQuotationMapper extends BaseMapper<PaperQuotation> {
    @Update("UPDATE paper_quotation SET delete_time = #{delete_time} WHERE quoter_id = #{quoter_id} OR quoted_id = #{quoted_id};")
    void deletePaper(@Param("delete_time")String deleteTime,@Param("quoter_id")Long quoterId,@Param("quoted_id")Long quotedId);

}
