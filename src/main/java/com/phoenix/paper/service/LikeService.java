package com.phoenix.paper.service;


public interface LikeService {
    Long like(Long objectId, Integer type, Long userId);

    Long cancelLike(Long objectId, Integer type, Long userId);

    Long getLikeNumber(Long objectId, Integer type);

}