package com.chenmeng.project.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author 沉梦听雨
 * @date 2023/06/19
 */
@Data
public class UserLoginRequest implements Serializable {

    /**
     * 用户帐户
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    private static final long serialVersionUID = 1L;
}
