package com.phoenix.paper.service;

import com.phoenix.paper.common.Page;
import com.phoenix.paper.controller.request.UpdateUserRequest;
import com.phoenix.paper.controller.response.LoginResponse;
import com.phoenix.paper.dto.BriefUser;
import com.phoenix.paper.dto.SessionData;
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
    String sendEmail(String email);

    LoginResponse signUp(String email,String password,String verificationCode);

    SessionData getUserById(Long userId);
//
    Integer updateUser(Long userId, UpdateUserRequest updateUserRequest);
//
//    void classifyUser(Long organizerId,Long userId,Long adminId);
}
