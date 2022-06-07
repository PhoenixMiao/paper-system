package com.phoenix.paper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("Likes 点赞记录")
public class Likes {
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("点赞id")
    private Long id;

    @ApiModelProperty("对象id")
    private Long objectId;

    @ApiModelProperty("对象类型(0为论文，1为笔记)")
    private Integer objectType;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("点赞时间")
    private String likeTime;

    @ApiModelProperty("删除时间")
    private String deleteTime;
}