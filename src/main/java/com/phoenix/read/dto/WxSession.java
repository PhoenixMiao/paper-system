package com.phoenix.read.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yannis
 * @version 2020/11/6 21:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WxSession {

    private String openId;
    private String sessionKey;
    private String unionId;
    private Integer errcode;
    private String errmsg;

}
