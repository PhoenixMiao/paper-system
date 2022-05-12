package com.phoenix.paper.controller.request;

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
@ApiModel("SearchPaperRequest 搜索论文")
public class SearchPaperRequest {

    @ApiModelProperty("论文标题")
    private String title;

    @ApiModelProperty("论文摘要")
    private String summary;

    @ApiModelProperty("论文作者")
    private String author;

    @ApiModelProperty("研究方向id列表")
    private long[] researchDirectionIds;
}
