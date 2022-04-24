package com.phoenix.paper.service;

import com.phoenix.paper.common.Page;
import com.phoenix.paper.controller.request.UpdateUserRequest;
import com.phoenix.paper.controller.response.LoginResponse;
import com.phoenix.paper.dto.BriefUser;
import com.phoenix.paper.entity.User;

public interface UserService {
    /**
     * 登录
     *
     * @param number,password
     * @return
     */
    LoginResponse login(String number, String password);

    Page<BriefUser> getBriefUserList(int pageSize, int pageNum,Long userId);
//
//    void toAdmin(Long userId,Long adminId);
//
//    void backToUser(Long userId,Long adminId);
//
    User getUserById(Long userId,Long targetId);
//
    void updateUser(Long userId, UpdateUserRequest updateUserRequest);
//
//    void classifyUser(Long organizerId,Long userId,Long adminId);
}
