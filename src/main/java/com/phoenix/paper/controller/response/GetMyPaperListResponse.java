package com.phoenix.paper.controller.response;

import com.phoenix.paper.common.Page;
import com.phoenix.paper.dto.BriefPaper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("GetMyPaperListResponse 获取我的论文列表")
public class GetMyPaperListResponse {

    @ApiModelProperty("我的论文")
    Page<BriefPaper> myPaper;

    @ApiModelProperty("总论文数")
    Long totalPaperNumber;

    @ApiModelProperty("近一周发布论文数")
    Long paperInThusWeek;
}
