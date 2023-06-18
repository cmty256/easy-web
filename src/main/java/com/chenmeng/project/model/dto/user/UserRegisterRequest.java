package com.chenmeng.project.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author 沉梦听雨
 * @date 2023/06/18
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L; // 序列化版本号

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
