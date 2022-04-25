package com.phoenix.paper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("comment 评论记录")

public class Comment {
    @Id
    @GeneratedValue(generator = "JDBC",strategy = GenerationType.IDENTITY)
    @ApiModelProperty("评论id")
    private Long id;

    @ApiModelProperty("笔记id")
    private Long noteId;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("评论时间")
    private String createTime;

    @ApiModelProperty("删除时间")
    private String  deleteTime;

    @ApiModelProperty("评论内容")
    private String comment;

}
