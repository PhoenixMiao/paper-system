package com.phoenix.paper.entity;

import com.baomidou.mybatisplus.annotation.*;
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
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("收藏id")
    private Long id;

    @ApiModelProperty("对象id")
    private Long objectId;

    @ApiModelProperty("对象类型(0为论文,1为笔记)")
    private Integer objectType;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("收藏时间")
    private String collectTime;

    @ApiModelProperty("删除时间")
    private String deleteTime;

    @Version
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("收藏乐观锁组件")
    private Integer version;
}
