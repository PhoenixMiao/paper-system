package com.phoenix.read.controller.response;

import com.phoenix.read.dto.SessionData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("LoginResponse 登录")
public class LoginResponse {
    @ApiModelProperty("会话存储信息（后端redis缓存，随时可以取出）")
    private SessionData sessionData;

    @ApiModelProperty("会话id（之后请求头中需要加入session-sessionId的键值对")
    private String sessionId;
}
