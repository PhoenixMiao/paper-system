package com.phoenix.read.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Id;


/**
 * @author zhuyan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("activity 活动")
public class Activity {
    @Id
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("活动名称")
    private String name;

    @ApiModelProperty("类型")
    private Integer type;

    @ApiModelProperty("状态 0:预约中  1:进行中  -1:已结束 ")
    private Integer status;

    @ApiModelProperty("发布人id")
    private Long publisherId;

    @ApiModelProperty("主办方id")
    private Long organizerId;

    @ApiModelProperty("活动开始时间")
    private String startTime;

    @ApiModelProperty("活动结束时间")
    private String endTime;

    @ApiModelProperty("活动地点")
    private String place;

    @ApiModelProperty("活动时长")
    private Integer time;

    @ApiModelProperty("预约开始时间")
    private String orderStartTime;

    @ApiModelProperty("预约结束时间")
    private String orderEndTime;

    @ApiModelProperty("预约人数")
    private Integer people;

    @ApiModelProperty("活动简介")
    private String introduction;

    @ApiModelProperty("是否签到")
    private Integer isCheck;

    @ApiModelProperty("参与人数乐观锁组件")
    private Integer version;
}
