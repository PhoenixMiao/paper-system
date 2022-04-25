package com.phoenix.paper.entity;

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
@ApiModel("Paper 论文")
public class Paper {
    @Id
    @GeneratedValue(generator = "JDBC",strategy = GenerationType.IDENTITY)
    @ApiModelProperty("论文id")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("作者")
    private String author;

    @ApiModelProperty("发布会议")
    private String publishConference;

    @ApiModelProperty("发布日期")
    private String publishDate;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("论文类型")
    private Integer paperType;

    @ApiModelProperty("文献链接")
    private String link;


    @ApiModelProperty("上传者id")
    private Long uploaderId;

    @ApiModelProperty("上传时间")
    private String uploadTime;

    @ApiModelProperty("删除时间")
    private String deleteTime;

    @ApiModelProperty("附加文件")
    private String fileLink;


}
