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
@ApiModel("member 参与情况")
public class Member {
    @Id
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("参与成员id")
    private Long userId;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("预约时间")
    private String createTime;
}
