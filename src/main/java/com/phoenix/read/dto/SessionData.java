package com.phoenix.read.dto;

import com.phoenix.read.common.CommonErrorCode;
import com.phoenix.read.entity.User;
import com.phoenix.read.util.AssertUtil;
import com.phoenix.read.util.TimeUtil;
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


    public SessionData(User user){
        AssertUtil.notNull(user, CommonErrorCode.USER_NOT_EXIST);
        this.id = user.getId();
        this.accountNum = user.getAccountNum();
    }
}
