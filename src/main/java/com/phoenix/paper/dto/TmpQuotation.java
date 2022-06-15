package com.phoenix.paper.dto;

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
@ApiModel("TmpQuotation 临时论文引用关系")
public class TmpQuotation {

    @ApiModelProperty("引用关系id")
    private Long id;

    @ApiModelProperty("发起引用的文献")
    private Long quoterId;

    @ApiModelProperty("被引用的文献")
    private Long quotedId;

    @ApiModelProperty("被引用论文的标题")
    private String title;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("创建时间")
    private String createTime;
}
