package com.phoenix.read.service;

public interface CollectionService {
    Long collect(Long passageId,Long userId);

    String cancelCollection(Long id);
}
