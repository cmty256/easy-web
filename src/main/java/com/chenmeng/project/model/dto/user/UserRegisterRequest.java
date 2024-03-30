package com.chenmeng.project.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author chenmeng
 * @date 2023/06/18
 */
@Data
public class UserRegisterRequest implements Serializable {

    /**
     * 用户帐户
     */
    private String userAccount;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String confirmPassword;

    private static final long serialVersionUID = 1L;
}
