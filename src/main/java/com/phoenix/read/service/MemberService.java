package com.phoenix.read.service;

import com.phoenix.read.common.Page;
import com.phoenix.read.dto.Order;
import com.phoenix.read.entity.Member;

public interface MemberService {
    Long makeOrder(Long activityId,Long userId);

    void check(Long id);

    Page<Order> getMemberListByUserId(int pageSize, int pageNum, Long userId);

    Member getMemberById(Long id);
}
