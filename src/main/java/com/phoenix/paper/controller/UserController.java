package com.phoenix.paper.controller;

import com.phoenix.paper.annotation.Admin;
import com.phoenix.paper.annotation.Auth;
import com.phoenix.paper.common.CommonException;
import com.phoenix.paper.common.Result;
import com.phoenix.paper.controller.request.UpdateUserRequest;
import com.phoenix.paper.dto.BriefUser;
import com.phoenix.paper.dto.SessionData;
import com.phoenix.paper.service.UserService;
import com.phoenix.paper.util.RedisUtils;
import com.phoenix.paper.util.SessionUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Api("用户相关操作")
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("/login")
    @ApiOperation(value = "登录",response = SessionData.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "用户账号",required = true,paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "query", dataType = "Integer"),})
    public Result login(@NotNull @RequestParam("number")String number,
                        @NotNull @RequestParam("password")String password){
        try{
            return Result.success(userService.login(number, password));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Admin
    @GetMapping("/list")
    @ApiOperation(value = "获取用户列表",response = BriefUser.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize",value = "每页显示数量 (不小于0)",required = true,paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),})
    public Result getBriefUserList(@NotNull @RequestParam("pageSize")Integer pageSize,
                                   @NotNull @RequestParam("pageNum")Integer pageNum){
        try{
            return Result.success(userService.getBriefUserList(pageSize,pageNum, sessionUtils.getUserId()));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Auth
    @GetMapping("/info")
    @ApiOperation(value = "获取我的信息")
    public Result getMyInformation(){

        try{
            return Result.success(userService.getUserById(sessionUtils.getUserId()));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Admin
    @GetMapping("/userInfo")
    @ApiOperation(value = "获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true,paramType = "query",dataType = "Long"),})
    public Result getUserInformation(@RequestParam("userId")Long userId){
        try{
            return Result.success(userService.getUserById(userId));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

//
//    @Auth
//    @GetMapping("/admin")
//    @ApiOperation(value = "超管将普通用户改为管理员",response = Long.class)
//    @ApiImplicitParam(name = "userId",value = "所需要被设置的用户的id",required = true,paramType = "query")
//    public Result toAdmin(@NotNull@RequestParam("userId")Long userId){
//        try{
//            userService.toAdmin(userId,sessionUtils.getUserId());
//            return Result.success(userId);
//        }catch (CommonException e){
//            return Result.result(e.getCommonErrorCode());
//        }
//    }
//
//    @Auth
//    @GetMapping("")
//    @ApiOperation(value = "获取个人信息")
//    public Result getUsergetSessionData(){
//        return Result.success(sessionUtils.getSessionData());
//    }
//

    @Auth
    @PostMapping("/update")
    @ApiOperation(value = "更改用户信息")
    public Result updateUser(@NotNull @RequestBody UpdateUserRequest updateUserRequest){
        try{
            return Result.success(userService.updateUser(sessionUtils.getUserId(), updateUserRequest));
        }catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Admin
    @GetMapping("/delete")
    @ApiOperation(value = "删除用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true,paramType = "query",dataType = "Long"),})
    public Result deleteUser(@NotNull @RequestParam("userId")Long userId){
        try{
            userService.deleteUser(userId);
        }catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
        return Result.success("删除成功");
    }

    @Admin
    @GetMapping("/authorize")
    @ApiOperation(value = "设置用户权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true,paramType = "query",dataType = "Long"),
            @ApiImplicitParam(name = "type",value = "权限(0普通用户 1管理员)",required = true,paramType = "query",dataType = "Integer"),})
    public Result authorizeUser(@NotNull @RequestParam("userId")Long userId,@NotNull @RequestParam("type")Integer type){
        try{
            userService.authorizeUser(userId,type);
        }catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
        return Result.success("设置成功");
    }

    @GetMapping("/send")
    @ApiOperation(value = "发送验证邮箱")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "emailOrNumber",value = "注册和找回账号输入邮箱，找回密码输入账号",required = true,paramType = "query"),
            @ApiImplicitParam(name = "type",value = "0为注册，1为找回账号，2为找回密码",required = true,paramType = "query"),
    })
    public Result sendEmail(@NotNull @RequestParam("emailOrNumber")String emailOrNumber,
                            @NotNull @RequestParam("type")int type){
        try{
            return Result.success(userService.sendEmail(emailOrNumber,type));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @PostMapping("/signUp")
    @ApiOperation(value = "注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email",value = "用户邮箱",required = true,paramType = "query"),
            @ApiImplicitParam(name = "password",value = "密码",required = true,paramType = "query")
    })
    public Result signUp(@NotNull @RequestParam("email")String email,
                         @NotNull @RequestParam("password")String password){
        try{
            return Result.success(userService.signUp(email, password));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @GetMapping("/check")
    @ApiOperation(value = "校验验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email",value = "用户邮箱",required = true,paramType = "query"),
            @ApiImplicitParam(name = "code",value = "邮箱验证码",required = true,paramType = "query")
    })
    public Result checkCode(@NotNull @RequestParam("email")String email,
                         @NotNull @RequestParam("code")String code){
        try{
            userService.checkCode(email,code);
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
        return Result.success("验证码正确");
    }

    @GetMapping("/number")
    @ApiOperation(value = "找回账号")
    @ApiImplicitParam(name = "email",value = "用户邮箱",required = true,paramType = "query")
    public Result findNumber(@NotNull @RequestParam("email")String email){
        return Result.success(userService.findNumber(email));
    }

    @PostMapping("/password")
    @ApiOperation(value = "更新密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "账号",required = true,paramType = "query"),
            @ApiImplicitParam(name = "password",value = "新密码",required = true,paramType = "query")
    })
    public Result updatePassword(@NotNull @RequestParam("number")String number,
                                 @NotNull @RequestParam("password")String password){
        try{
            userService.updatePassword(number,password);
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
        return Result.success("更新成功");
    }

//    @Auth
//    @GetMapping("/user")
//    @ApiOperation(value = "超管将管理员改为普通用户",response = Long.class)
//    @ApiImplicitParam(name = "userId",value = "所需要被设置的用户的id",required = true,paramType = "query")
//    public Result backToUser(@NotNull@RequestParam("userId")Long userId){
//        try{
//            userService.backToUser(userId,sessionUtils.getUserId());
//            return Result.success(userId);
//        }catch (CommonException e){
//            return Result.result(e.getCommonErrorCode());
//        }
//    }
//
//    @Auth
//    @GetMapping("/organize")
//    @ApiOperation(value = "超管将用户绑定到特定主办方中",response = Long.class)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userId",value = "所需要被设置的用户的id",required = true,paramType = "query"),
//            @ApiImplicitParam(name = "organizerId",value = "主办方id",required = true,paramType = "query")
//    })
//    public Result backToUser(@NotNull@RequestParam("userId")Long userId,
//                             @NotNull @RequestParam("organizerId")Long organizerId){
//        try{
//            userService.classifyUser(organizerId,userId,sessionUtils.getUserId());
//            return Result.success(userId);
//        }catch (CommonException e){
//            return Result.result(e.getCommonErrorCode());
//        }
//    }
}
