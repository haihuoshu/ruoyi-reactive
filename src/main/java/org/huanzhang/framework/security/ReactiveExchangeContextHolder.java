package org.huanzhang.framework.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class ReactiveExchangeContextHolder {

    private static final Class<?> EXCHANGE_CONTEXT_KEY = ServerWebExchange.class;

    private static boolean hasExchangeContext(Context context) {
        return context.hasKey(EXCHANGE_CONTEXT_KEY);
    }

    private static Mono<ServerWebExchange> getExchangeContext(Context context) {
        return context.<Mono<ServerWebExchange>>get(EXCHANGE_CONTEXT_KEY);
    }

    public static Context withExchangeContext(Mono<? extends ServerWebExchange> securityContext) {
        return Context.of(EXCHANGE_CONTEXT_KEY, securityContext);
    }

    public static Mono<ServerWebExchange> getExchange() {
        return Mono.deferContextual(Mono::just)
                .cast(Context.class)
                .filter(ReactiveExchangeContextHolder::hasExchangeContext)
                .flatMap(ReactiveExchangeContextHolder::getExchangeContext);
    }

    public static Mono<ServerHttpRequest> getRequest() {
        return getExchange().
                map(ServerWebExchange::getRequest);
    }

}
