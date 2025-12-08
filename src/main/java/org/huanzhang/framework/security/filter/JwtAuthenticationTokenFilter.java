package org.huanzhang.framework.security.filter;

import jakarta.annotation.Resource;
import org.huanzhang.common.utils.SecurityUtils;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.framework.security.LoginUser;
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
        Authentication authentication = SecurityUtils.getAuthentication();

        LoginUser loginUser = tokenService.getLoginUser(exchange.getRequest());
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(authentication)) {
            tokenService.verifyToken(loginUser);
            authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        }
        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }
}
