package com.phoenix.paper.dto;


import com.phoenix.paper.entity.Paper;
import com.phoenix.paper.entity.PaperDirection;
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
@ApiModel("PaperAndNoteData 论文、笔记数据")
public class PaperAndNoteData {

    @ApiModelProperty("研究方向")
    private String direction;

    @ApiModelProperty("数量")
    private Integer number;
}
