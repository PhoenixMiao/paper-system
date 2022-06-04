package com.phoenix.paper.dto;

import com.phoenix.paper.entity.Paper;
import com.phoenix.paper.entity.PaperDirection;
import com.phoenix.paper.entity.PaperQuotation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("DetailedPaper 论文详情（比paper多研究方向和文献引用）")
public class DetailedPaper {
    @ApiModelProperty("论文本身")
    private Paper paper;

    @ApiModelProperty("研究方向")
    private List<PaperDirection> paperDirectionList;

    @ApiModelProperty("文献引用")
    private List<PaperQuotation> paperQuotationList;

    @ApiModelProperty("是否有修改和删除权限")
    private boolean canModify;
}
