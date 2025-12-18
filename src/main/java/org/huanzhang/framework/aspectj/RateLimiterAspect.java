package org.huanzhang.framework.aspectj;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.huanzhang.common.exception.ServiceException;
import org.huanzhang.common.utils.ip.IpUtils;
import org.huanzhang.framework.aspectj.lang.annotation.RateLimiter;
import org.huanzhang.framework.aspectj.lang.enums.LimitType;
import org.huanzhang.framework.security.ReactiveExchangeContextHolder;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 限流处理
 *
 * @author ruoyi
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimiterAspect {

    private final ReactiveRedisOperations<String, Object> reactiveRedisOperations;

    private final RedisScript<Long> limitScript;

    @Around("@annotation(org.huanzhang.framework.aspectj.lang.annotation.RateLimiter)")
    public Mono<Object> doAround(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RateLimiter rateLimiter = signature.getMethod().getAnnotation(RateLimiter.class);

        int time = rateLimiter.time();
        int count = rateLimiter.count();

        return ReactiveExchangeContextHolder.getRequest()
                .flatMap(request -> {
                    String combineKey = getCombineKey(rateLimiter, joinPoint, request);
                    List<String> keys = Collections.singletonList(combineKey);
                    try {
                        return reactiveRedisOperations.execute(limitScript, keys, count, time)
                                .next()
                                .defaultIfEmpty(Long.MAX_VALUE)
                                .map(number -> {
                                    if (number > count) {
                                        return new ServiceException("访问过于频繁，请稍候再试");
                                    }
                                    log.info("限制请求'{}',当前请求'{}',缓存key'{}'", count, number, combineKey);
                                    try {
                                        return joinPoint.proceed();
                                    } catch (Throwable e) {
                                        return new RuntimeException(e);
                                    }
                                });
                    } catch (ServiceException e) {
                        return Mono.error(e);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("服务器限流异常，请稍候再试"));
                    }
                });
    }

    public String getCombineKey(RateLimiter rateLimiter, JoinPoint point, ServerHttpRequest request) {
        StringBuilder stringBuffer = new StringBuilder(rateLimiter.key());
        if (rateLimiter.limitType() == LimitType.IP) {
            stringBuffer.append(IpUtils.getIpAddr(request)).append("-");
        }
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        stringBuffer.append(targetClass.getName()).append("-").append(method.getName());
        return stringBuffer.toString();
    }
}
