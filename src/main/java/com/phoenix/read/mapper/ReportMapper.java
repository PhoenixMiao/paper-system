package com.phoenix.read.mapper;

import com.phoenix.read.MyMapper;
import com.phoenix.read.entity.Push;
import com.phoenix.read.entity.Report;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;

@Repository
public interface ReportMapper  extends MyMapper<Report> {
    @Update("UPDATE report SET status=#{status} WHERE id=#{id}")
    void examReport(@Param("status")Integer status,@Param("id")Long id);
}
