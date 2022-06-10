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
@ApiModel("NoteSumPerDay 笔记数据每日统计")
public class NoteSumPerDay {
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("统计时间")
    private String sumTime;

    @ApiModelProperty("笔记方向")
    private String direction;

    @ApiModelProperty("周笔记数量")
    private Integer number_week;

    @ApiModelProperty("月笔记数量")
    private Integer number_month;

    @ApiModelProperty("年笔记数量")
    private Integer number_year;

}
