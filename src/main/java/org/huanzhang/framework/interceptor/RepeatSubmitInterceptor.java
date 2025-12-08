package org.huanzhang.framework.interceptor;

import org.huanzhang.framework.interceptor.annotation.RepeatSubmit;
import org.huanzhang.framework.web.domain.R;
import org.huanzhang.framework.webflux.utils.WebFluxUtils;
import org.springframework.core.annotation.AnnotatedMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 防止重复提交拦截器
 *
 * @author ruoyi
 */
public abstract class RepeatSubmitInterceptor implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 获取HandlerMethod（需要配合RequestMappingHandlerMapping）
        return Mono.justOrEmpty(exchange.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE))
                .switchIfEmpty(chain.filter(exchange))
                .cast(HandlerMethod.class)
                .map(AnnotatedMethod::getMethod)
                .flatMap(method -> {
                    RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
                    if (Objects.nonNull(annotation)) {
                        return isRepeatSubmit(request, annotation)
                                .flatMap(isRepeat -> {
                                    if (isRepeat) {
                                        // 返回重复提交错误响应
                                        return WebFluxUtils.writeBodyAsString(response, R.fail(annotation.message()));
                                    } else {
                                        return chain.filter(exchange);
                                    }
                                });
                    }
                    return chain.filter(exchange);
                });
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     *
     * @param request    请求信息
     * @param annotation 防重复注解参数
     * @return 结果
     */
    public abstract Mono<Boolean> isRepeatSubmit(ServerHttpRequest request, RepeatSubmit annotation);

}
