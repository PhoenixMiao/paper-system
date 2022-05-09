package com.phoenix.paper.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.phoenix.paper.entity.Collection;

public interface CollectionService {

    Long collect(Long objectId, Integer type, Long userId);

    Long cancelCollect(Long objectId, Integer type, Long userId);

    Long getCollectNumber(Long objectId, Integer type);

    IPage<Collection> getCollectionList(Integer pageSize, Integer pageNum, Long UserId);
}
