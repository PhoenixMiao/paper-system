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
@ApiModel("NewPushRequest 新建新活动的推送")
public class NewPushRequest {

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("类型(0:人文 1:社科 2:理工 3:其他)")
    private int type;

    @ApiModelProperty("图片")
    private String picture;

    @ApiModelProperty("来源")
    private String source;

    @ApiModelProperty("活动id（活动预告推送不用填写）")
    private Long activityId;
}
