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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private HttpServletRequest request;

    @GetMapping(value = "/login" , produces = "application/json")
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
    @GetMapping(value = "/list",produces = "application/json")
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
    @GetMapping(value = "/info",produces = "application/json")
    @ApiOperation(value = "获取我的信息")
    public Result getMyInformation(){

        try{
            return Result.success(userService.getUserById(sessionUtils.getUserId()));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Admin
    @GetMapping(value = "/userInfo",produces = "application/json")
    @ApiOperation(value = "获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "query", dataType = "Long"),})
    public Result getUserInformation(@RequestParam("userId") Long userId) {
        try {
            return Result.success(userService.getUserById(userId));
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }

//todo
//    @Admin
//    @GetMapping(value = "/admin",produces = "application/json")
//    @ApiOperation(value = "超管将普通用户改为管理员",response = Long.class)
//    @ApiImplicitParam(name = "userId",value = "所需要被设置的用户的id",required = true,paramType = "query")
//    public Result toAdmin(@NotNull@RequestParam("userId")Long userId){
//        try{
//            userService.toAdmin(userId);
//            return Result.success(userId);
//        }catch (CommonException e){
//            return Result.result(e.getCommonErrorCode());
//        }
//    }

    @Auth
    @GetMapping(value = "", produces = "application/json")
    @ApiOperation(value = "获取个人信息")
    public Result getUserSessionData() {
        try {
            return Result.success(sessionUtils.getSessionData());
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }

    }


    @Auth
    @PostMapping(value = "/update",produces = "application/json")
    @ApiOperation(value = "更改用户信息")
    public Result updateUser(@NotNull @RequestBody UpdateUserRequest updateUserRequest){
        try{
            userService.updateUser(sessionUtils.getUserId(), updateUserRequest);
            return Result.success("更新成功");
        }catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }

    @Auth
    @PostMapping(value = "/delete",produces = "application/json")
    @ApiOperation(value = "注销用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true,paramType = "query",dataType = "Long"),})
    public Result deleteUser(@NotNull @RequestParam("userId") Long userId) {
        try {
            userService.deleteUser(userId);
            if (sessionUtils.getSessionData().getType() == 0) redisUtils.del(request.getHeader("session"));
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
        return Result.success("删除成功");
    }

//todo
//    @Admin
//    @GetMapping(value = "/authorize",produces = "application/json")
//    @ApiOperation(value = "设置用户权限")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userId",value = "用户id",required = true,paramType = "query",dataType = "Long"),
//            @ApiImplicitParam(name = "type",value = "权限(0普通用户 1管理员)",required = true,paramType = "query",dataType = "Integer"),})
//    public Result authorizeUser(@NotNull @RequestParam("userId")Long userId,@NotNull @RequestParam("type")Integer type){
//        try{
//            userService.authorizeUser(userId,type);
//        }catch (CommonException e) {
//            return Result.result(e.getCommonErrorCode());
//        }
//        return Result.success("设置成功");
//    }

    @GetMapping(value = "/send", produces = "application/json")
    @ApiOperation(value = "发送验证邮箱")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "emailOrNumber", value = "注册和找回账号输入邮箱，找回密码输入账号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "0为注册，1为找回账号，2为找回密码", required = true, paramType = "query"),
    })
    public Result sendEmail(@NotNull @RequestParam("emailOrNumber") String emailOrNumber,
                            @NotNull @RequestParam("type") int type) {
        try {
            return Result.success(userService.sendEmail(emailOrNumber,type));
        }catch (CommonException e){
            return Result.result(e.getCommonErrorCode());
        }
    }

    @PostMapping(value = "/signUp",produces = "application/json")
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

    @GetMapping(value = "/check",produces = "application/json")
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

    @GetMapping(value = "/number",produces = "application/json")
    @ApiOperation(value = "找回账号")
    @ApiImplicitParam(name = "email",value = "用户邮箱",required = true,paramType = "query")
    public Result findNumber(@NotNull @RequestParam("email")String email){
        return Result.success(userService.findNumber(email));
    }

    @PostMapping( value = "/password",produces = "application/json")
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

    @Auth
    @PostMapping(value = "/email",produces = "application/json")
    @ApiOperation(value = "更改邮箱")
    @ApiImplicitParam(name = "email", value = "用户邮箱", required = true, paramType = "query")
    public Result changeEmail(@NotNull @RequestParam("email") String email) {
        try {
            userService.updateEmail(email, sessionUtils.getUserId());
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
        return Result.success("更改成功");
    }

    @Auth
    @GetMapping(value = "/paper", produces = "application/json")
    @ApiOperation(value = "获取该用户的论文列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", required = true, paramType = "query", dataType = "Integer"),
    })
    public Result getUserPaperList(@NotNull @RequestParam("pageSize") Integer pageSize, @NotNull @Param("pageNum") Integer pageNum) {
        try {
            return Result.success(userService.getUserPaperList(pageNum, pageSize, sessionUtils.getUserId()));
        } catch (CommonException e) {
            return Result.result(e.getCommonErrorCode());
        }
    }
}
