package com.chenmeng.project.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户密码找回请求体
 *
 * @author 沉梦
 */
@Data
public class UserPasswBackRequest implements Serializable {

    /**
     * 用户帐户
     */
    private String userAccount;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 确认密码
     */
    private String confirmPassword;
}
