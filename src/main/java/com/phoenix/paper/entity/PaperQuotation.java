package com.phoenix.paper.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
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
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
    @ApiModelProperty("引用关系id")
    private Long id;

    @ApiModelProperty("发起引用的文献")
    private Long quoterId;

    @ApiModelProperty("被引用的文献")
    private Long quotedId;

    //todo 不知道正不正确
    @TableField(strategy = FieldStrategy.IGNORED)
    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("删除时间")
    private String deleteTime;

    @Version
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("论文引用信息乐观锁组件")
    private Integer version;
}
