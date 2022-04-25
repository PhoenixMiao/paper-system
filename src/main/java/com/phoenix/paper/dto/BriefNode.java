package com.phoenix.paper.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("BriefNode 子节点列表")
public class BriefNode {
    @ApiModelProperty("节点id")
    private Long id;

    @ApiModelProperty("节点名称")
    private String name;

    @ApiModelProperty("是否是叶子")
    private Integer isLeaf;
}
