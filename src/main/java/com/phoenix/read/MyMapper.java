package com.phoenix.read;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 通用mapper统一实现MyMapper
 * @author yannis
 * @version 2020/8/1 23:43
 */
public interface MyMapper<T> extends Mapper<T>,MySqlMapper<T> {
    
}
