package com.phoenix.paper.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;


/**
 * @author lishuai
 * @version 2021/2/2 21:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("User 用户")
public class User {
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("用户id")
    private Long id;

    @ApiModelProperty("账号")
    private String accountNum;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("注销时间")
    private String deleteTime;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("真实姓名")
    private String name;

    @ApiModelProperty("性别")
    private Integer gender;

    @ApiModelProperty("学校")
    private String school;

    @ApiModelProperty("专业")
    private String major;

    @ApiModelProperty("年级")
    private String grade;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("电话号")
    private String telephone;

    @ApiModelProperty("头像")
    private String portrait;

    @ApiModelProperty("类型(0为普通用户，1为管理员)")
    private Integer type;

    @Version
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("用户信息乐观锁组件")
    private int version;

    @ApiModelProperty("此周论文数")
    private int paperWeekNum;

    @ApiModelProperty("此周笔记数")
    private int noteWeekNum;

    @ApiModelProperty("发布论文总数")
    private int paperNum;

    @ApiModelProperty("发布笔记总数")
    private int noteNum;

    @ApiModelProperty("评论权限")
    private int canComment;

    @ApiModelProperty("修改/删除权限")
    private int canModify;
}
