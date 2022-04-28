package com.phoenix.paper.service;

public interface CollectionService {

    Long collect(Long objectId, Integer type, Long userId);

    Long cancelCollect(Long objectId, Integer type, Long userId);

    Long getCollectNumber(Long objectId, Integer type);
}
