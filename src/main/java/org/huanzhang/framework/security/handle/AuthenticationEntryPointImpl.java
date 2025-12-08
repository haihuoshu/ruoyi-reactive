package org.huanzhang.framework.security.handle;

import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.framework.web.domain.R;
import org.huanzhang.framework.webflux.utils.WebFluxUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 认证失败处理类 返回未授权
 *
 * @author ruoyi
 */
@Component
public class AuthenticationEntryPointImpl implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        int code = HttpStatus.UNAUTHORIZED.value();
        String msg = StringUtils.format("请求访问：{}，认证失败，无法访问系统资源", exchange.getRequest().getURI().getPath());
        return WebFluxUtils.writeBodyAsString(exchange.getResponse(), R.fail(code, msg));
    }
}
