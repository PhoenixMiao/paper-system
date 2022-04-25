package com.phoenix.paper.entity;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("PaperDirection 论文研究方向")
public class PaperDirection {
    @Id
    @GeneratedValue(generator = "JDBC",strategy = GenerationType.IDENTITY)
    @ApiModelProperty("方向id")
    private Long id;

    @ApiModelProperty("论文id")
    private Long paperId;

    @ApiModelProperty("研究方向id")
    private Long directionId;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("删除时间")
    private String deleteTime;
}
