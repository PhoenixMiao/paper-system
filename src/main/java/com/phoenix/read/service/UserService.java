package com.phoenix.read.service;

import com.phoenix.read.common.Page;
import com.phoenix.read.controller.request.UpdateUserRequest;
import com.phoenix.read.controller.response.LoginResponse;
import com.phoenix.read.dto.BriefUser;
import com.phoenix.read.dto.SessionData;
import com.phoenix.read.entity.User;
import org.springframework.stereotype.Service;

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
