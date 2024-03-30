package com.chenmeng.project.model.dto.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;


/**
 * 用户更新请求体
 *
 * @author chenmeng
 * @date 2023/06/19
 */
@Data
public class UserUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 注解：表示该字段不是数据库表中的列
     * 可以不写
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}