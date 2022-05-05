package com.phoenix.paper.service;

import com.phoenix.paper.common.Page;
import com.phoenix.paper.controller.request.UpdateUserRequest;
import com.phoenix.paper.controller.response.LoginResponse;
import com.phoenix.paper.dto.BriefUser;
import com.phoenix.paper.dto.SessionData;
import com.phoenix.paper.entity.User;
import io.swagger.models.auth.In;

public interface UserService {
    /**
     * 登录
     *
     * @param number,password
     * @return
     */
    LoginResponse login(String number, String password);

    Page<BriefUser> getBriefUserList(int pageSize, int pageNum,Long userId);

    void toAdmin(Long userId);

    String sendEmail(String email,int flag);

    LoginResponse signUp(String email,String password);

    void checkCode(String email,String code);

    String findNumber(String email);

    void updatePassword(String accountNum,String password);

    SessionData getUserById(Long userId);

    Integer updateUser(Long userId, UpdateUserRequest updateUserRequest);

    void authorizeUser(Long userId, Integer type);

    void deleteUser(Long userId);
}
