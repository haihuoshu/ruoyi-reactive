package org.huanzhang.framework.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

public interface SysAccessLogApi {

    /**
     * 新增访问日志
     */
    Mono<Long> insertAccessLog(ServerHttpRequest request, String username, String status, String message, Object... args);

}
