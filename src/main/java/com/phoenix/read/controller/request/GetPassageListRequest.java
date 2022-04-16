package com.phoenix.read.controller.request;

import com.phoenix.read.common.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("GetPassageListRequest 获取论坛列表")
public class GetPassageListRequest {
    @ApiModelProperty("类别 1:交流 2:答疑 3:综合")
    private Integer type;

    @ApiModelProperty("子类别 1:人文艺术 2:社会科学 3:自然科学")
    private Integer subtype;

    @ApiModelProperty("排序顺序 1:最新 2:热门")
    @Min(value = 1, message = "order字段必须为1或2")
    @Max(value = 2, message = "order字段必须为1或2")
    private Integer order;

    @ApiModelProperty("分页参数")
    private PageParam pageParam;

}
