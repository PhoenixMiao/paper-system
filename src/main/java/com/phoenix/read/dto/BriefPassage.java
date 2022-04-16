package com.phoenix.read.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("BriefPassage 论坛列表")
public class BriefPassage {
    @Id
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("编写用户id")
    private Long userId;

    @ApiModelProperty("正文")
    private String content;

    @ApiModelProperty("标题")
    private String title;
}
