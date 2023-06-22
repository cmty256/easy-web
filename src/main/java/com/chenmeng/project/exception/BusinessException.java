package com.chenmeng.project.exception;

import com.chenmeng.project.common.ErrorCode;

/**
 * 自定义异常类
 *
 * @author 沉梦听雨
 * @date 2023/06/18
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super((errorCode.getMessage()));
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }

}
