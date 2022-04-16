package com.phoenix.read.dto;

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
@ApiModel("Order 个人预约或活动列表")
public class Order {
    @ApiModelProperty("成员id")
    private Long memberId;

    @ApiModelProperty("活动名称")
    private String name;

    @ApiModelProperty("主办方")
    private String organizer;

    @ApiModelProperty("活动发布者姓名")
    private String publisher;

    @ApiModelProperty("活动开始时间")
    private String startTime;

    @ApiModelProperty("活动状态(0为未开始，1为进行中，-1为已结束)")
    private Integer status;
}
