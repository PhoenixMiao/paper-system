package com.phoenix.paper.controller.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("AddPaperRequest 添加论文请求")
public class AddPaperRequest {

    @NotNull
    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("作者")
    private String author;

    @ApiModelProperty("发布会议")
    private String publishConference;

    @ApiModelProperty("论文发布日期（真实）")
    private String publishDate;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("论文类型(0:证明型,1:综述型,2:实验型,3:工具型,4:数据集型)")
    private Integer paperType;

    @ApiModelProperty("文献链接(一个网址、指向其他网站中论文的所在（如知网）)")
    private String link;

    @ApiModelProperty("研究方向id列表")
    private List<Long> researchDirectionList;
}
