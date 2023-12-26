package com.chenmeng.project.aop;

import com.chenmeng.project.common.utils.ClassUtil;
import com.chenmeng.project.common.utils.JsonUtil;
import com.chenmeng.project.common.utils.WebUtil;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 请求日志切面
 *
 * @author 沉梦听雨
 */
@Aspect
@Component
public class RequestLogAspect {
    private static final Logger log = LoggerFactory.getLogger(RequestLogAspect.class);

    @Around("execution(* com.chenmeng.project.controller.*.*(..))")
    public Object aroundApi(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = WebUtil.getRequest();
        String requestUrl = (Objects.requireNonNull(request)).getRequestURI();
        String requestMethod = request.getMethod();
        StringBuilder beforeReqLog = new StringBuilder(300);
        List<Object> beforeReqArgs = new ArrayList();
        beforeReqLog.append("\n============== Request Start ==============\n");
        beforeReqLog.append("Method:{} URL:{}");
        beforeReqArgs.add(requestMethod);
        beforeReqArgs.add(requestUrl);
        this.logIngArgs(point, beforeReqLog, beforeReqArgs);
        this.logIngHeaders(request, beforeReqLog, beforeReqArgs);
        beforeReqLog.append("==============  Request End  ==============\n");
        long startNs = System.nanoTime();
        log.info(beforeReqLog.toString(), beforeReqArgs.toArray());
        StringBuilder afterReqLog = new StringBuilder(200);
        List<Object> afterReqArgs = new ArrayList();
        afterReqLog.append("\n============== Response Start ==============\n");
        boolean var20 = false;

        Object var13;
        try {
            var20 = true;
            Object result = point.proceed();

            var13 = result;
            var20 = false;
        } finally {
            if (var20) {
                long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
                afterReqLog.append("Method:{} URL:{} ({} ms)\n");
                afterReqArgs.add(requestMethod);
                afterReqArgs.add(requestUrl);
                afterReqArgs.add(tookMs);
                afterReqLog.append("==============  Response End  ==============\n");
                log.info(afterReqLog.toString(), afterReqArgs.toArray());
            }
        }

        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        afterReqLog.append("Method:{} URL:{} ({} ms)\n");
        afterReqArgs.add(requestMethod);
        afterReqArgs.add(requestUrl);
        afterReqArgs.add(tookMs);
        afterReqLog.append("==============  Response End  ==============\n");
        log.info(afterReqLog.toString(), afterReqArgs.toArray());
        return var13;

    }

    public void logIngArgs(ProceedingJoinPoint point, StringBuilder beforeReqLog, List<Object> beforeReqArgs) throws IllegalAccessException {
        MethodSignature ms = (MethodSignature) point.getSignature();
        Method method = ms.getMethod();
        Object[] args = point.getArgs();
        Map<String, Object> paraMap = new HashMap(16);
        Object requestBodyValue = null;

        for (int i = 0; i < args.length; ++i) {
            MethodParameter methodParam = ClassUtil.getMethodParameter(method, i);
            PathVariable pathVariable = methodParam.getParameterAnnotation(PathVariable.class);
            if (pathVariable == null) {
                RequestBody requestBody = methodParam.getParameterAnnotation(RequestBody.class);
                String parameterName = methodParam.getParameterName();
                Object value = args[i];
                if (requestBody != null) {
                    requestBodyValue = value;
                } else if (value instanceof HttpServletRequest) {
                    paraMap.putAll(((HttpServletRequest) value).getParameterMap());
                } else if (value instanceof WebRequest) {
                    paraMap.putAll(((WebRequest) value).getParameterMap());
                } else if (!(value instanceof HttpServletResponse)) {
                    String paraName;
                    if (value instanceof MultipartFile) {
                        MultipartFile multipartFile = (MultipartFile) value;
                        paraName = multipartFile.getName();
                        String fileName = multipartFile.getOriginalFilename();
                        paraMap.put(paraName, fileName);
                    } else {
                        if (value instanceof List) {
                            List<?> list = (List) value;
                            AtomicBoolean isSkip = new AtomicBoolean(false);
                            Iterator var17 = list.iterator();

                            while (var17.hasNext()) {
                                Object o = var17.next();
                                if ("StandardMultipartFile".equalsIgnoreCase(o.getClass().getSimpleName())) {
                                    isSkip.set(true);
                                    break;
                                }
                            }

                            if (isSkip.get()) {
                                paraMap.put(parameterName, "此参数不能序列化为json");
                                continue;
                            }
                        }

                        RequestParam requestParam = methodParam.getParameterAnnotation(RequestParam.class);
                        paraName = parameterName;
                        if (requestParam != null && StringUtils.isNotBlank(requestParam.value())) {
                            paraName = requestParam.value();
                        }

                        if (value == null) {
                            paraMap.put(paraName, null);
                        } else if (ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
                            paraMap.put(paraName, value);
                        } else if (value instanceof InputStream) {
                            paraMap.put(paraName, "InputStream");
                        } else if (value instanceof InputStreamSource) {
                            paraMap.put(paraName, "InputStreamSource");
                        } else if (JsonUtil.canSerialize(value)) {
                            Map<String, Object> dtoMap = new HashMap<>(16);
                            for (Field field : value.getClass().getDeclaredFields()) {
                                field.setAccessible(true);
                                Object fieldValue = field.get(value);
                                if (field.getType().isAssignableFrom(MultipartFile.class) && fieldValue != null) {
                                    MultipartFile multipartFile = (MultipartFile) fieldValue;
                                    String multipartFileFiledName = multipartFile.getName();
                                    String fileName = multipartFile.getOriginalFilename();
                                    dtoMap.put(multipartFileFiledName, fileName);
                                } else {
                                    dtoMap.put(field.getName(), fieldValue);
                                }
                            }
                            paraMap.put(paraName, dtoMap);
                        } else {
                            paraMap.put(paraName, "此参数不能序列化为json");
                        }
                    }
                }
            }
        }

        if (paraMap.isEmpty()) {
            beforeReqLog.append("\n");
        } else {
            beforeReqLog.append(" Parameters: {}\n");
            beforeReqArgs.add(JsonUtil.toJson(paraMap));
        }

        if (requestBodyValue != null) {
            beforeReqLog.append("Body:{}\n");
            beforeReqArgs.add(JsonUtil.toJson(requestBodyValue));
        }

    }

    public void logIngHeaders(HttpServletRequest request, StringBuilder beforeReqLog, List<Object> beforeReqArgs) {
        Enumeration<String> headers = request.getHeaderNames();

        while (headers.hasMoreElements()) {
            String headerName = headers.nextElement();
            String headerValue = request.getHeader(headerName);
            beforeReqLog.append("{}: {}\n");
            beforeReqArgs.add(headerName);
            beforeReqArgs.add(headerValue);
        }
    }

}
