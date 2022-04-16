package com.phoenix.read.controller.response;

import com.phoenix.read.entity.Passage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("GetPassageResponse 论坛详情")
public class GetPassageResponse {
    @ApiModelProperty("论坛")
    private Passage passage;

    @ApiModelProperty("评论id列表")
    private List<Long> commentsId;
}
