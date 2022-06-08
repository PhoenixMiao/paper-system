package com.phoenix.paper.controller.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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

    @NotNull
    @ApiModelProperty("每页显示数量")
    @Min(value = 0,message = "每页显示数量应非负")
    private Integer pageSize;

    @NotNull
    @ApiModelProperty("页数")
    @Min(value = 1,message = "页数应为正数")
    private Integer pageNum;

    @NotNull
    @ApiModelProperty("排序规则(0为热度,1为笔记发布时间)")
    @Min(value = 0, message = "热度排序")
    @Max(value = 1, message = "发布时间排序")
    private Integer orderby;
}
