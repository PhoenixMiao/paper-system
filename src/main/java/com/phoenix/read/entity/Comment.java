package com.phoenix.read.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Id;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("comment 评论记录")

public class Comment {
    @Id
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("评论用户")
    private Long userId;

    @ApiModelProperty("类别")
    private Integer objectType;

    @ApiModelProperty("评论内容")
    private String comment;

    @ApiModelProperty("评论对象的id")
    private Long objectId;

    @ApiModelProperty("评论时间")
    private String commentTime;

}
