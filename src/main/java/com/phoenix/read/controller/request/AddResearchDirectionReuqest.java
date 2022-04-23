package com.phoenix.read.controller.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("AddResearchDirectionReuqest 添加研究方向请求")
public class AddResearchDirectionReuqest {

    @NotNull
    @ApiModelProperty("研究方向名称")
    private String name;

    @ApiModelProperty("根节点id")
    private Long rootId;

    @ApiModelProperty("父方向id")
    private Long fatherId;

    @ApiModelProperty("树id")
    private Long treeId;

    @ApiModelProperty("节点高度")
    private Integer height;

    @ApiModelProperty("节点层数id")
    private Long layer_id;

    @ApiModelProperty("节点路径")
    private String path;
}
