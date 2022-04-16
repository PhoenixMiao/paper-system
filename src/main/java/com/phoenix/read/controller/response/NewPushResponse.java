package com.phoenix.read.controller.response;

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
@ApiModel("NewPushResponse 新建新活动的推送")
public class NewPushResponse {
    @ApiModelProperty("推送id")
    private Long pushId;

    @ApiModelProperty("活动id")
    private Long activityId;
}
