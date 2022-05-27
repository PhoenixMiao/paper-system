package com.phoenix.paper.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("BriefUser 用户列表")
public class BriefUser {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("真实姓名")
    private String name;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("头像")
    private String portrait;

    @ApiModelProperty("类型(0为普通用户，1为管理员)")
    private Integer type;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("删除时间")
    private String deleteTime;

    @ApiModelProperty("评论权限")
    private int canComment;

    @ApiModelProperty("修改/删除权限")
    private int canModify;

}
