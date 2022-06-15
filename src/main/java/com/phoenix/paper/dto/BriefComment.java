package com.phoenix.paper.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("BriefComment 评论列表")
public class BriefComment {

    @ApiModelProperty("评论id")
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("评论id")
    private Long commentId;

    @ApiModelProperty("笔记id")
    private Long noteId;

    @ApiModelProperty("评论时间")
    private String createTime;

    @ApiModelProperty("评论内容")
    private String contents;
}
