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
@ApiModel("BriefNote 笔记列表")
public class BriefNote {

    @ApiModelProperty("笔记id")
    private Long id;

    @ApiModelProperty("创建者id")
    private Long authorId;

    @ApiModelProperty("创建者昵称")
    private String author;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("封面图片")
    private String cover;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("点赞数")
    private Integer likeNumber;

    @ApiModelProperty("收藏数")
    private Integer collectNumber;
}
