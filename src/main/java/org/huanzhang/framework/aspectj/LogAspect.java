package org.huanzhang.framework.aspectj;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.huanzhang.framework.aspectj.lang.annotation.Log;
import org.huanzhang.framework.security.ReactiveExchangeContextHolder;
import org.huanzhang.framework.security.ReactiveSecurityUtils;
import org.huanzhang.framework.security.SysOperateLogApi;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 操作日志记录处理
 *
 * @author ruoyi
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final SysOperateLogApi sysOperateLogApi;

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
                                                        sysOperateLogApi.insertOperateLog(request, loginUser, joinPoint, controllerLog, null, jsonResult, start)
                                                                .thenReturn(jsonResult)
                                                )
                                                .onErrorResume(e ->
                                                        sysOperateLogApi.insertOperateLog(request, loginUser, joinPoint, controllerLog, e, null, start)
                                                                .then(Mono.error(e))
                                                );
                                    } else if (proceed instanceof Flux<?> flux) {
                                        return flux.collectList()
                                                .flatMap(jsonResult ->
                                                        sysOperateLogApi.insertOperateLog(request, loginUser, joinPoint, controllerLog, null, jsonResult, start)
                                                                .thenReturn(jsonResult)
                                                )
                                                .onErrorResume(e ->
                                                        sysOperateLogApi.insertOperateLog(request, loginUser, joinPoint, controllerLog, e, null, start)
                                                                .then(Mono.error(e))
                                                );
                                    }

                                    return sysOperateLogApi.insertOperateLog(request, loginUser, joinPoint, controllerLog, null, proceed, start)
                                            .thenReturn(proceed);
                                } catch (Throwable e) {
                                    return sysOperateLogApi.insertOperateLog(request, loginUser, joinPoint, controllerLog, e, null, start)
                                            .then(Mono.error(e));
                                }
                            });
                });
    }

}
