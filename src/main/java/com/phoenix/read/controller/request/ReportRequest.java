package com.phoenix.read.controller.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("ReportRequest 举报请求")
public class ReportRequest {
    @NotNull
    @ApiModelProperty("论坛id")
    private Long passageId;

    @ApiModelProperty("举报理由")
    private String reason;
}
