package com.phoenix.paper.controller.request;

import com.phoenix.paper.common.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("SearchNoteRequest 搜索笔记")
public class SearchNoteRequest {

    @ApiModelProperty("笔记标题")
    private String title;

    @ApiModelProperty("笔记作者")
    private String author;

    @ApiModelProperty("相关文献")
    private String paper;

    @NotNull
    @ApiModelProperty("分页参数")
    private PageParam pageParam;
}
