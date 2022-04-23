package com.phoenix.read.entity;

import cn.hutool.core.date.DateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("ResearchDirection 研究方向")
public class ResearchDirection {

    @Id
    @ApiModelProperty("研究方向id")
    private Long id;

    @ApiModelProperty("研究方向名称")
    private String name;

    @ApiModelProperty("根节点id")
    private Long rootId;

    @ApiModelProperty("父方向id")
    private Long fatherId;

    @ApiModelProperty("树id")
    private Long treeId;

    @ApiModelProperty("节点层id")
    private Long layerId;

    @ApiModelProperty("节点高度")
    private Integer height;

    @ApiModelProperty("节点层数id")
    private Long layer_id;

    @ApiModelProperty("节点路径")
    private String path;

    @ApiModelProperty("创建者id")
    private Long creatorId;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("删除时间")
    private String deleteTime;
}
