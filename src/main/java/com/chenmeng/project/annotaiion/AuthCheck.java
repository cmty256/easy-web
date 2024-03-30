package com.chenmeng.project.annotaiion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解 - 权限校验
 *
 * @author chenmeng
 * @date 2023/06/22
 */
@Target(ElementType.METHOD) // 表示该注解只能添加在方法上
@Retention(RetentionPolicy.RUNTIME) // 表示该注解在运行时可用
public @interface AuthCheck {

    /**
     * 有任何一个角色
     *
     * @return {@code String[]}
     */
    String[] anyRole() default "";

    /**
     * 必须有某个角色
     *
     * @return {@code String}
     */
    String mustRole() default "";

}
