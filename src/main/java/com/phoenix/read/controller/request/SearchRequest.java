package com.phoenix.read.controller.request;

import com.phoenix.read.common.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bytebuddy.asm.Advice;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("SearchRequest 搜索请求")
public class SearchRequest {
    @Min(value = 1, message = "type字段必须为1或2")
    @Max(value = 2, message = "type字段必须为1或2")
    @ApiModelProperty("搜索类型（1为帖子，2为推送）")
    Integer type;

    @ApiModelProperty("主办方")
    private String organizer;

    @ApiModelProperty("发布者")
    private String publisher;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("正文")
    private String content;

    @NotNull
    @ApiModelProperty("分页参数")
    private PageParam pageParam;
}
