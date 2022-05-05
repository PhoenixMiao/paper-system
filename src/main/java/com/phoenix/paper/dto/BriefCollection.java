package com.phoenix.paper.dto;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("BriefCollection 收藏列表")
public class BriefCollection {
    @ApiModelProperty("收藏id")
    private Long id;

    @ApiModelProperty("对象id")
    private Long objectId;

    @ApiModelProperty("对象类型(0为论文,1为笔记)")
    private Integer objectType;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("收藏时间")
    private String  collectTime;
}
