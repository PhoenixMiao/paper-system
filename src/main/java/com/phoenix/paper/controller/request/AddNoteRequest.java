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
@ApiModel("AddNoteRequest 添加笔记请求")
public class AddNoteRequest {

    @ApiModelProperty("文献id")
    private Long paperId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("html文本")
    private String html;

    @ApiModelProperty("text文本")
    private String text;
}
