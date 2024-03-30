package com.chenmeng.project.common.utils;

import com.chenmeng.project.common.BaseResponse;
import com.chenmeng.project.common.enums.ErrorCode;

/**
 * 返回结果工具类
 *
 * @author chenmeng
 * @date 2023/06/19
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data 数据
     * @return {@code BaseResponse<T>}
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code 状态码
     * @param message 信息
     * @return
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @param message 信息
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse(errorCode.getCode(), null, message);
    }
}
