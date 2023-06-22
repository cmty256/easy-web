package com.chenmeng.project.common;

/**
 * 统一错误码
 *
 * @author 沉梦听雨
 * @date 2023/06/17
 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"), // params_error

    NOT_LOGIN_ERROR(40100, "未登录"), // not_login_error

    NO_AUTH_ERROR(40101, "无权限"), // not_auth_error
    
    NOT_FOUND_ERROR(40400, "请求的数据不存在"), // not_found_error
    
    FORBIDDEN_EEOR(40300, "禁止访问"), // forbidden_error

    SYSTEM_ERROR(50000, "系统内部异常"), // system_error

    OPERATION_ERROR(50001, "操作失败"); // operation_error

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取错误状态码
     *
     * @return int
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取错误响应信息
     *
     * @return {@code String}
     */
    public String getMessage() {
        return message;
    }

}
