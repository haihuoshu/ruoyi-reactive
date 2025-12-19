package org.huanzhang.framework.security.handle;

import lombok.RequiredArgsConstructor;
import org.huanzhang.common.constant.Constants;
import org.huanzhang.framework.security.SysAccessLogApi;
import org.huanzhang.framework.security.service.TokenService;
import org.huanzhang.framework.web.domain.AjaxResponse;
import org.huanzhang.framework.webflux.utils.WebFluxUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 自定义退出处理类 返回成功
 *
 * @author ruoyi
 */
@Component
@RequiredArgsConstructor
public class LogoutSuccessHandlerImpl implements ServerLogoutSuccessHandler {

    private final TokenService tokenService;
    private final SysAccessLogApi sysAccessLogApi;

    /**
     * 退出处理
     */
    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        return tokenService.getLoginUser(exchange.getExchange().getRequest())
                .flatMap(loginUser -> {
                    String userName = loginUser.getUsername();
                    // 删除用户缓存记录
                    tokenService.delLoginUser(loginUser.getToken());
                    // 记录用户退出日志
                    return sysAccessLogApi.insertAccessLog(exchange.getExchange().getRequest(), userName, Constants.LOGOUT, "退出成功");
                })
                .then(WebFluxUtils.writeBodyAsString(exchange.getExchange().getResponse(), AjaxResponse.ok(null, "退出成功")));
    }
}
