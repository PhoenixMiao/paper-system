package com.phoenix.paper.service;

import com.phoenix.paper.common.Page;
import com.phoenix.paper.dto.BriefCollection;
import com.phoenix.paper.dto.BriefPaper;

public interface CollectionService {

    Long collect(Long objectId, Integer type, Long userId);

    Long cancelCollect(Long objectId, Integer type, Long userId);

    Long getCollectNumber(Long objectId, Integer type);

    Page<BriefCollection> getCollectionList(Integer pageSize, Integer pageNum, Long UserId);
}
