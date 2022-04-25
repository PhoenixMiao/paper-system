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
@ApiModel("PaperQuotation 论文引用关系")
public class PaperQuotation {
    @Id
    @GeneratedValue(generator = "JDBC",strategy = GenerationType.IDENTITY)
    @ApiModelProperty("引用关系id")
    private Long id;

    @ApiModelProperty("发起引用的文献")
    private Long quoterId;

    @ApiModelProperty("被引用的文献")
    private Long quotedId;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("删除时间")
    private String deleteTime;

}
