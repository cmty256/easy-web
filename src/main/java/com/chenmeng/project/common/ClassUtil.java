package com.chenmeng.project.common;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.SynthesizingMethodParameter;

import java.lang.reflect.Method;

/**
 * Class工具类
 *
 * @author 沉梦听雨
 **/
public class ClassUtil {

    /**
     * 创建一个ParameterNameDiscoverer对象，用于获取参数名
     */
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    /**
     * 获取方法参数名称
     *
     * @param method
     * @param parameterIndex
     * @return
     */
    public static MethodParameter getMethodParameter(Method method, int parameterIndex) {
        // 创建一个MethodParameter对象
        MethodParameter methodParameter = new SynthesizingMethodParameter(method, parameterIndex);
        // 初始化参数名称发现器
        methodParameter.initParameterNameDiscovery(PARAMETER_NAME_DISCOVERER);
        // 返回MethodParameter对象
        return methodParameter;
    }
}
