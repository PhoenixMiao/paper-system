package com.phoenix.read.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("帖子id")
    private Long passageId;

    @ApiModelProperty("收藏时间")
    private String collectTime;
}
