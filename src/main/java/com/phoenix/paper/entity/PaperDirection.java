package com.phoenix.paper.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@ApiModel("PaperDirection 论文研究方向")
public class PaperDirection {
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("论文方向id")
    private Long id;

    @ApiModelProperty("论文id")
    private Long paperId;

    @ApiModelProperty("研究方向id")
    private Long directionId;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("删除时间")
    private String deleteTime;

    @Version
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("论文方向信息乐观锁组件")
    private Integer version;
}
