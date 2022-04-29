package com.phoenix.paper.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("BriefPaper 论文列表")
public class BriefPaper {

    @ApiModelProperty("论文id")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("发布日期（平台上用户的上传时间）")
    private String publishDate;

    @ApiModelProperty("论文发布日期（真实）")
    private String paperDate;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("文献链接")
    private String link;

}
