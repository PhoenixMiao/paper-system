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
@ApiModel("push 推送")

public class Push {
    @Id
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("标题")
        private String title;

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("活动类型")
    private Integer activityType;

    @ApiModelProperty("发布日期")
    private String publishDate;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("类型")
    private int type;

    @ApiModelProperty("图片")
    private String picture;

}