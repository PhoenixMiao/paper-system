package com.phoenix.read.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Param;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("BriefPush 推送列表")
public class BriefPush {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("图片")
    private String pic;

    @ApiModelProperty("分类（0为预告，1为总结）")
    private Integer type;

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("活动分类")
    private Integer activityType;

    public BriefPush(Long id, String title, String pic, Integer type, Long activityId) {
        this.id = id;
        this.title = title;
        this.pic = pic;
        this.type = type;
        this.activityId = activityId;
    }
}
