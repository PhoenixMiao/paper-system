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
@ApiModel("report 举报")
public class Report {
    @Id
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("论坛id")
    private Long passageId;

    @ApiModelProperty("举报理由")
    private String reason;

    @ApiModelProperty("状态 0未处理 1举报成功 -1举报失败")
    private Integer status;

    @ApiModelProperty("举报时间")
    private String reportTime;
}
