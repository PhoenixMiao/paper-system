package com.phoenix.read.controller.request;


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
@ApiModel("CommentRequest 请求评论")
public class CommentRequest {
    @ApiModelProperty("对象类别 0帖子 1评论")
    private Integer objectType;

    @ApiModelProperty("评论内容")
    private String comment;

    @ApiModelProperty("评论对象的id")
    private Long objectId;
}
