package com.chenmeng.project.common;

import com.chenmeng.project.common.enums.ErrorCode;
import lombok.Data;

import java.io.Serializable;


/**
 * 通用返回类
 *
 * @author 沉梦听雨
 * @date 2023/06/17
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code; // 状态码

    private T data; // 数据

    private String message; // 响应信息

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
