package com.phoenix.paper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("Note 笔记")
public class Note {
    @Id
    @GeneratedValue(generator = "JDBC",strategy = GenerationType.IDENTITY)
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

    @Value("${some.key:0}")
    @ApiModelProperty("点赞数")
    private Long likeNumber;

    @Value("${some.key:0}")
    @ApiModelProperty("收藏数")
    private Long collectNumber;
}
