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
@ApiModel("SearchNote 搜索使用的笔记实体")
public class SearchNote {
    @ApiModelProperty("笔记id")
    private Long id;

    @ApiModelProperty("创建者用户名")
    private String author;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("所链接的论文")
    private SearchPaper searchPaper;
}
