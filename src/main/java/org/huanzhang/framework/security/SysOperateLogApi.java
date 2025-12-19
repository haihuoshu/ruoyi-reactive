package org.huanzhang.framework.security;

import org.aspectj.lang.JoinPoint;
import org.huanzhang.framework.aspectj.lang.annotation.Log;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

public interface SysOperateLogApi {

    /**
     * 新增操作日志
     */
    Mono<Long> insertOperateLog(ServerHttpRequest request, LoginUser loginUser, JoinPoint joinPoint, Log controllerLog, Throwable e, Object jsonResult, long start);

}
