package org.huanzhang.framework.security.filter;

import jakarta.annotation.Resource;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.framework.security.ReactiveExchangeContextHolder;
import org.huanzhang.framework.security.service.TokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * token过滤器 验证token有效性
 *
 * @author ruoyi
 */
@Component
public class JwtAuthenticationTokenFilter implements WebFilter {

    @Resource
    private TokenService tokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Authentication unauthenticated = UsernamePasswordAuthenticationToken.unauthenticated(null, null);

        return tokenService.getLoginUser(exchange.getRequest())
                .map(loginUser -> {
                    if (StringUtils.isNotNull(loginUser)) {
                        tokenService.verifyToken(loginUser);
                        return UsernamePasswordAuthenticationToken.authenticated(loginUser, null, loginUser.getAuthorities());
                    }
                    return unauthenticated;
                })
                .defaultIfEmpty(unauthenticated)
                .flatMap(authentication -> {
                    // 设置
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                            .contextWrite(ReactiveExchangeContextHolder.withExchangeContext(Mono.just(exchange)));
                });
    }

}
