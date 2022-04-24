package com.phoenix.paper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * @author zhuyan
 * @version
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("collection 收藏")

public class Collection {
    @Id
    @GeneratedValue(generator = "JDBC")
    @ApiModelProperty("收藏id")
    private Long id;

    @ApiModelProperty("对象id")
    private Long objectId;

    @ApiModelProperty("对象类型")
    private Integer objectType;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("收藏时间")
    private String  collectTime;

    @ApiModelProperty("删除时间")
    private String  deleteTime;
}
