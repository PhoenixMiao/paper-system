package com.phoenix.paper.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("ResearchDirection 研究方向")
public class ResearchDirection {
    @Id
    @GeneratedValue(generator = "JDBC",strategy = GenerationType.IDENTITY)
    @ApiModelProperty("研究方向id")
    private Long id;

    @ApiModelProperty("研究方向名称")
    private String name;

    @ApiModelProperty("根节点id")
    private Long rootId;

    @ApiModelProperty("父方向id")
    private Long fatherId;

    @ApiModelProperty("节点路径")
    private String path;

    @ApiModelProperty("是否是叶子")
    private Integer isLeaf;

    @ApiModelProperty("创建者id")
    private Long creatorId;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("删除时间")
    private String deleteTime;

    @Version
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("研究方向信息乐观锁组件")
    private Integer version;
}
