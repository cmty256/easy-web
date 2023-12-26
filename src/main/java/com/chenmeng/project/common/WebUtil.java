package com.chenmeng.project.common;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Web工具类
 *
 * @author 沉梦听雨
 **/
public class WebUtil {

    /**
     * 获取HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        // 获取请求属性
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 如果请求属性为空，返回null，否则返回ServletRequestAttributes的请求
        return requestAttributes == null ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
    }
}
