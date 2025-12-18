package org.huanzhang.framework.aspectj;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.huanzhang.common.core.text.Convert;
import org.huanzhang.common.enums.HttpMethod;
import org.huanzhang.common.filter.PropertyPreExcludeFilter;
import org.huanzhang.common.utils.ExceptionUtil;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.common.utils.ip.IpUtils;
import org.huanzhang.framework.aspectj.lang.annotation.Log;
import org.huanzhang.framework.aspectj.lang.enums.BusinessStatus;
import org.huanzhang.framework.manager.factory.AsyncFactory;
import org.huanzhang.framework.security.LoginUser;
import org.huanzhang.framework.security.ReactiveExchangeContextHolder;
import org.huanzhang.framework.security.ReactiveSecurityUtils;
import org.huanzhang.project.monitor.domain.SysOperLog;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;

/**
 * 操作日志记录处理
 *
 * @author ruoyi
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * 排除敏感属性字段
     */
    public static final String[] EXCLUDE_PROPERTIES = {"password", "oldPassword", "newPassword", "confirmPassword"};

    /**
     * 处理请求前执行
     */
    @Around(value = "@annotation(org.huanzhang.framework.aspectj.lang.annotation.Log)")
    public Mono<?> doAround(ProceedingJoinPoint joinPoint) {
        // 获取方法注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Log controllerLog = signature.getMethod().getAnnotation(Log.class);

        long start = System.currentTimeMillis();

        // 获取Request
        return ReactiveExchangeContextHolder.getRequest()
                .flatMap(request -> {
                    // 获取登录用户
                    return ReactiveSecurityUtils.getLoginUser()
                            .flatMap(loginUser -> {
                                try {
                                    Object proceed = joinPoint.proceed();

                                    if (proceed instanceof Mono<?> mono) {
                                        return mono
                                                .flatMap(jsonResult ->
                                                        handleLog(request, loginUser, joinPoint, controllerLog, null, jsonResult, start)
                                                                .thenReturn(jsonResult)
                                                )
                                                .onErrorResume(e ->
                                                        handleLog(request, loginUser, joinPoint, controllerLog, e, null, start)
                                                                .then(Mono.error(e))
                                                );
                                    } else if (proceed instanceof Flux<?> flux) {
                                        return flux.collectList()
                                                .flatMap(jsonResult ->
                                                        handleLog(request, loginUser, joinPoint, controllerLog, null, jsonResult, start)
                                                                .thenReturn(jsonResult)
                                                )
                                                .onErrorResume(e ->
                                                        handleLog(request, loginUser, joinPoint, controllerLog, e, null, start)
                                                                .then(Mono.error(e))
                                                );
                                    }

                                    return handleLog(request, loginUser, joinPoint, controllerLog, null, proceed, start)
                                            .thenReturn(proceed);
                                } catch (Throwable e) {
                                    return handleLog(request, loginUser, joinPoint, controllerLog, e, null, start)
                                            .then(Mono.error(e));
                                }
                            });
                });
    }

    private Mono<Object> handleLog(ServerHttpRequest request, LoginUser loginUser, final JoinPoint joinPoint, Log controllerLog, final Throwable e, Object jsonResult, long start) {

        // *========数据库日志=========*//
        SysOperLog operLog = new SysOperLog();
        operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
        // 请求的地址
        String ip = IpUtils.getIpAddr(request);
        operLog.setOperIp(ip);
        operLog.setOperUrl(StringUtils.substring(request.getURI().getPath(), 0, 255));
        operLog.setOperName(loginUser.getUsername());
//        SysUser currentUser = loginUser.getUser();
//        if (StringUtils.isNotNull(currentUser) && StringUtils.isNotNull(currentUser.getDept())) {
//            operLog.setDeptName(currentUser.getDept().getDeptName());
//        }

        if (e != null) {
            operLog.setStatus(BusinessStatus.FAIL.ordinal());
            operLog.setErrorMsg(StringUtils.substring(Convert.toStr(e.getMessage(), ExceptionUtil.getExceptionMessage(e)), 0, 2000));
        }
        // 设置方法名称
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        operLog.setMethod(className + "." + methodName + "()");
        // 设置请求方式
        operLog.setRequestMethod(request.getMethod().name());
        // 处理设置注解上的参数
        getControllerMethodDescription(request, joinPoint, controllerLog, operLog, jsonResult);
        // 设置消耗时间
        operLog.setCostTime(System.currentTimeMillis() - start);
        // 保存数据库
        AsyncFactory.recordOper(operLog);
        return Mono.empty();
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     */
    public void getControllerMethodDescription(ServerHttpRequest request, JoinPoint joinPoint, Log log, SysOperLog operLog, Object jsonResult) {
        // 设置action动作
        operLog.setBusinessType(log.businessType().ordinal());
        // 设置标题
        operLog.setTitle(log.title());
        // 设置操作人类别
        operLog.setOperatorType(log.operatorType().ordinal());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(request, joinPoint, operLog, log.excludeParamNames());
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData() && StringUtils.isNotNull(jsonResult)) {
            operLog.setJsonResult(StringUtils.substring(JSON.toJSONString(jsonResult), 0, 2000));
        }
    }

    /**
     * 获取请求的参数，放到log中
     */
    private void setRequestValue(ServerHttpRequest request, JoinPoint joinPoint, SysOperLog operLog, String[] excludeParamNames) {
        String requestMethod = operLog.getRequestMethod();
        Map<?, ?> paramsMap = request.getQueryParams();
        if (StringUtils.isEmpty(paramsMap) && StringUtils.equalsAny(requestMethod, HttpMethod.PUT.name(), HttpMethod.POST.name(), HttpMethod.DELETE.name())) {
            String params = argsArrayToString(joinPoint.getArgs(), excludeParamNames);
            operLog.setOperParam(StringUtils.substring(params, 0, 2000));
        } else {
            operLog.setOperParam(StringUtils.substring(JSON.toJSONString(paramsMap, excludePropertyPreFilter(excludeParamNames)), 0, 2000));
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null) {
            for (Object o : paramsArray) {
                if (StringUtils.isNotNull(o) && !isFilterObject(o)) {
                    try {
                        String jsonObj = JSON.toJSONString(o, excludePropertyPreFilter(excludeParamNames));
                        params.append(jsonObj).append(" ");
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 忽略敏感属性
     */
    public PropertyPreExcludeFilter excludePropertyPreFilter(String[] excludeParamNames) {
        return new PropertyPreExcludeFilter().addExcludes(ArrayUtils.addAll(EXCLUDE_PROPERTIES, excludeParamNames));
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
