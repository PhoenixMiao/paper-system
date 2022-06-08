package com.phoenix.paper.service;

import com.phoenix.paper.common.Page;
import com.phoenix.paper.controller.request.UpdateUserRequest;
import com.phoenix.paper.controller.response.LoginResponse;
import com.phoenix.paper.dto.BriefPaper;
import com.phoenix.paper.dto.BriefUser;
import com.phoenix.paper.dto.PaperAndNoteData;
import com.phoenix.paper.dto.SessionData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    /**
     * 登录
     *
     * @param number,password
     * @return
     */
    LoginResponse login(String number, String password);

    Page<BriefUser> getBriefUserList(int pageSize, int pageNum, Long userId);

    void toAdmin(Long userId);

    String sendEmail(String email,int flag);

    LoginResponse signUp(String email,String password);

    void checkCode(String email, String code);

    String findNumber(String email);

    void updatePassword(String accountNum, String password);

    SessionData getUserById(Long userId);

    void updateUser(Long userId, UpdateUserRequest updateUserRequest);

    void upgradeUser(Long userId, Integer canModify);

    void muteUser(Long userId);

    void deleteUser(Long userId);

    void updateEmail(String email, Long userId);

    Page<BriefPaper> getUserPaperList(Integer pageNum, Integer pageSize, Long userId);

    List<PaperAndNoteData> getUserPaperData(Integer period, Long userId);

    List<PaperAndNoteData> getUserNoteData(Integer period, Long userId);

    String uploadPortrait(MultipartFile file, Long userId);
}
