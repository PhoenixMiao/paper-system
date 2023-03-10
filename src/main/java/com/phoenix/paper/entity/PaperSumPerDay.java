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
@ApiModel("PaperSumPerDay 论文数据每日统计")
public class PaperSumPerDay {
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("统计时间")
    private String sumTime;

    @ApiModelProperty("论文方向")
    private String direction;

    @ApiModelProperty("笔记数量")
    private Integer number;
}
