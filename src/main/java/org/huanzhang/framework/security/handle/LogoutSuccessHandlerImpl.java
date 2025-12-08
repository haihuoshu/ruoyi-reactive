package org.huanzhang.framework.security.handle;

import jakarta.annotation.Resource;
import org.huanzhang.common.constant.Constants;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.framework.manager.factory.AsyncFactory;
import org.huanzhang.framework.security.LoginUser;
import org.huanzhang.framework.security.service.TokenService;
import org.huanzhang.framework.web.domain.R;
import org.huanzhang.framework.webflux.utils.WebFluxUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import reactor.core.publisher.Mono;

/**
 * 自定义退出处理类 返回成功
 *
 * @author ruoyi
 */
@Configuration
public class LogoutSuccessHandlerImpl implements ServerLogoutSuccessHandler {

    @Resource
    private TokenService tokenService;

    /**
     * 退出处理
     */
    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        LoginUser loginUser = tokenService.getLoginUser(exchange.getExchange().getRequest());
        if (StringUtils.isNotNull(loginUser)) {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            AsyncFactory.recordLogininfor(exchange.getExchange().getRequest(), userName, Constants.LOGOUT, "退出成功");
        }
        return WebFluxUtils.writeBodyAsString(exchange.getExchange().getResponse(), R.ok(null, "退出成功"));
    }
}
