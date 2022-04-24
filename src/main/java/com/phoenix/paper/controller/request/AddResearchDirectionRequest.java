package com.phoenix.paper.controller.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("AddResearchDirectionRequest 添加研究方向请求")
public class AddResearchDirectionRequest {

    @NotNull
    @ApiModelProperty("研究方向名称")
    private String name;

    @ApiModelProperty("父节点id（若添加根节点则写入0）")
    @Min(value = 0,message = "父节点id至少为0")
    private Long fatherId;
}
