package com.phoenix.paper.dto;

import com.phoenix.paper.entity.Paper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.phoenix.paper.common.CommonConstants.PAPER_TYPE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("SearchPaper 搜索使用的论文实体")
public class SearchPaper {

    @ApiModelProperty("论文id")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("作者")
    private String author;

    @ApiModelProperty("发布会议")
    private String publishConference;

    @ApiModelProperty("发布日期（真实）")
    private String publishDate;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("论文类型")
    private String paperType;

    @ApiModelProperty("文献链接")
    private String link;

    @ApiModelProperty("上传者用户名")
    private String uploader;

    @ApiModelProperty("论文内容")
    private String context;

    public SearchPaper(Paper paper) {
        this.id = paper.getId();
        this.author = paper.getAuthor();
        this.link = paper.getLink();
        this.paperType = PAPER_TYPE[paper.getPaperType()];
        this.publishConference = paper.getPublishConference();
        this.publishDate = paper.getPublishDate();
        this.summary = paper.getSummary();
        this.title = paper.getTitle();
    }
}
