package com.phoenix.read.common;

import lombok.Getter;

/**
 * @author yannis
 * @version 2020/7/23 0:59
 */
@Getter
public class CommonException extends RuntimeException {

    private CommonErrorCode commonErrorCode;

    private Object errorMsg;

    public CommonException() {
    }

    public CommonException(CommonErrorCode commonErrorCode) {
        this.commonErrorCode = commonErrorCode;
    }

    public CommonException(CommonErrorCode commonErrorCode, Object errorMsg) {
        this.commonErrorCode = commonErrorCode;
        this.errorMsg = errorMsg;
    }
}
