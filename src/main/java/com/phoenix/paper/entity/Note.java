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
@ApiModel("Note 笔记")
public class Note {
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("笔记id")
    private Long id;

    @ApiModelProperty("文献id")
    private Long paperId;

    @ApiModelProperty("创建者id")
    private Long authorId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("封面图片")
    private String cover;

    @ApiModelProperty("笔记html文本")
    private String html;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("删除时间")
    private String deleteTime;

    @ApiModelProperty("点赞数")
    private Integer likeNumber;

    @ApiModelProperty("收藏数")
    private Integer collectNumber;

    @Version
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("笔记信息乐观锁组件")
    private Integer version;
}
