package com.phoenix.read.mapper;

import com.phoenix.read.MyMapper;
import com.phoenix.read.entity.Organizer;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizerMapper extends MyMapper<Organizer> {
    @Select("SELECT * FROM organizer")
    List<Organizer> getOrganizerList();

    @Select("SELECT * FROM organizer WHERE name LIKE %#{name}%")
    List<Organizer> getOrganizerListByName(@Param("name")String name);
}
