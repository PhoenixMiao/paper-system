package com.phoenix.paper.controller.response;

import com.phoenix.paper.dto.SessionData;
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
@ApiModel("LoginResponse 登录")
public class LoginResponse {
    @ApiModelProperty("会话存储信息（后端redis缓存，随时可以取出）")
    private SessionData sessionData;

    @ApiModelProperty("会话id（之后需要登录才能操作的接口的请求的请求头中需要加入key为session,value为sessionId的键值对），" +
            "请把该字段缓存，因为系统如果忘记了，那就只能重新登录了。")
    private String sessionId;
}
