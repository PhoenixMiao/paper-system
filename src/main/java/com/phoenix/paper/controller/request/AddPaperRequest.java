package com.phoenix.paper.controller.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("AddPaperRequest 添加论文请求")
public class AddPaperRequest {

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("作者")
    private String author;

    @ApiModelProperty("发布会议")
    private String publishConference;

    @ApiModelProperty("论文发布日期（真实）")
    private String paperDate;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("论文类型")
    private Integer paperType;

    @ApiModelProperty("文献链接")
    private String link;

    @ApiModelProperty("上传者id")
    private Long uploaderId;

    @ApiModelProperty("附加文件")
    private String fileLink;
}
