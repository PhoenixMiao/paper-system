package com.phoenix.read.controller.request;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("NewActivityRequest 补全新活动的相关信息")
public class UpdateActivityRequest {
    @ApiModelProperty("活动id")
    private Long id;

    @ApiModelProperty("活动名称")
    @NotNull
    private String name;

    @ApiModelProperty("主办方id")
    @NotNull
    private Long organizerId;

    @ApiModelProperty("活动开始时间")
    @NotNull
    private String startTime;

    @ApiModelProperty("活动结束时间")
    @NotNull
    private String endTime;

    @ApiModelProperty("活动地点")
    @NotNull
    private String place;

    @ApiModelProperty("预约开始时间")
    @NotNull
    private String orderStartTime;

    @ApiModelProperty("预约结束时间")
    @NotNull
    private String orderEndTime;

    @ApiModelProperty("活动简介")
    private String introduction;

    @ApiModelProperty("是否签到")
    @NotNull
    private Integer isCheck;

}
