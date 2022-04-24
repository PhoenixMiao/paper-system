package com.phoenix.paper.entity;

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
@ApiModel("Note 笔记")
public class Note {
    @Id
    @GeneratedValue(generator = "JDBC")
    @ApiModelProperty("笔记id")
    private Long id;

    @ApiModelProperty("文献id")
    private Long paperId;

    @ApiModelProperty("笔记链接")
    private String noteLink;

    @ApiModelProperty("创建者id")
    private Long authorId;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("删除时间")
    private String deleteTime;
}
