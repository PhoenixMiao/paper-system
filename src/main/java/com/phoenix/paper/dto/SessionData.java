package com.phoenix.paper.dto;

import com.phoenix.paper.common.CommonErrorCode;
import com.phoenix.paper.entity.User;
import com.phoenix.paper.util.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * session缓存实体
 * @author yan on 2020-02-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("SessionData 会话实体")
public class SessionData implements Serializable {

    /**
     * {@link User}
     */
    @ApiModelProperty("用户id")
    private Long id;

    @ApiModelProperty("账号")
    private String accountNum;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("注销时间")
    private String deleteTime;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("真实姓名")
    private String name;

    @ApiModelProperty("性别")
    private Integer gender;

    @ApiModelProperty("学校")
    private String school;

    @ApiModelProperty("专业")
    private String major;

    @ApiModelProperty("年级")
    private String grade;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("电话号")
    private String telephone;

    @ApiModelProperty("头像")
    private String portrait;

    @ApiModelProperty("类型(0为普通用户，1为管理员)")
    private Integer type;

    @ApiModelProperty("此周论文数")
    private int paperWeekNum;

    @ApiModelProperty("此周笔记数")
    private int noteWeekNum;

    @ApiModelProperty("发布论文总数")
    private int paperNum;

    @ApiModelProperty("发布笔记总数")
    private int noteNum;


    public SessionData(User user){
        AssertUtil.notNull(user, CommonErrorCode.USER_NOT_EXIST);
        this.id = user.getId();
        this.accountNum = user.getAccountNum();
        this.type = user.getType();
        this.telephone = user.getTelephone();
        this.major = user.getMajor();
        this.school = user.getSchool();
        this.createTime = user.getCreateTime();
        this.deleteTime = user.getDeleteTime();
        this.email = user.getEmail();
        this.gender = user.getGender();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.portrait = user.getPortrait();
        this.noteWeekNum = user.getNoteWeekNum();
        this.paperWeekNum = user.getPaperWeekNum();
        this.noteNum = user.getNoteNum();
        this.paperNum = user.getPaperNum();
    }
}
