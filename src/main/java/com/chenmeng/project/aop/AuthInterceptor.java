package com.chenmeng.project.aop;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.chenmeng.project.annotaiion.AuthCheck;
import com.chenmeng.project.common.ErrorCode;
import com.chenmeng.project.exception.BusinessException;
import com.chenmeng.project.model.entity.User;
import com.chenmeng.project.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限校验拦截器 AOP
 *
 * @author 沉梦听雨
 * @date 2023/06/22
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint 连接点，即被拦截到的方法
     * @param authCheck 自定义注解 - 身份验证检查
     * @return {@code Object}
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取 @AuthCheck 注解的 anyRole 属性值，并将 非空字符串 添加到列表中。
        List<String> anyRole = Arrays.stream(authCheck.anyRole()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        // 获取 @AuthCheck 注解的 mustRole 属性值
        String mustRole = authCheck.mustRole();
        // 获取当前请求的属性
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        // 获取当前请求对象
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 获取当前登录用户
        User user = userService.getLoginUser(request);
        // 拥有任意权限即可通过
        if (CollectionUtils.isNotEmpty(anyRole)) { // 判断 anyRole 列表是否非空
            String userRole = user.getUserRole();
            if (!anyRole.contains(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 必须拥有所有权限才可通过
        if (StringUtils.isNotBlank(mustRole)) { // 判断 mustRole 列表是否非空
            String userRole = user.getUserRole();
            if (!mustRole.equals(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}
