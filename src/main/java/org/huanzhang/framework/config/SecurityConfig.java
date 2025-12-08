package org.huanzhang.framework.config;

import jakarta.annotation.Resource;
import org.huanzhang.framework.config.properties.PermitAllUrlProperties;
import org.huanzhang.framework.security.filter.JwtAuthenticationTokenFilter;
import org.huanzhang.framework.security.handle.AuthenticationEntryPointImpl;
import org.huanzhang.framework.security.handle.LogoutSuccessHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.web.cors.reactive.CorsWebFilter;

/**
 * spring security配置
 *
 * @author ruoyi
 */
@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {
    /**
     * 自定义用户认证逻辑
     */
    @Resource
    private UserDetailsService userDetailsService;

    /**
     * 认证失败处理类
     */
    @Resource
    private AuthenticationEntryPointImpl unauthorizedHandler;

    /**
     * 退出处理类
     */
    @Resource
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    /**
     * token认证过滤器
     */
    @Resource
    private JwtAuthenticationTokenFilter authenticationTokenFilter;

    /**
     * 跨域过滤器
     */
    @Resource
    private CorsWebFilter corsFilter;

    /**
     * 允许匿名访问的地址
     */
    @Resource
    private PermitAllUrlProperties permitAllUrl;

    /**
     * 身份验证实现
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Bean
    protected SecurityWebFilterChain filterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                // CSRF禁用，因为不使用session
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // 禁用HTTP响应标头
                .headers(headers -> headers.cache(ServerHttpSecurity.HeaderSpec.CacheSpec::disable)
                        .frameOptions(frameOptions -> frameOptions.mode(XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN))
                )
                // 认证失败处理类
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                // 基于token，所以不需要session
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                // 注解标记允许匿名访问的url
                .authorizeExchange((requests) -> {
                    permitAllUrl.getUrls().forEach(url -> requests.pathMatchers(url).permitAll());
                    // 对于登录login 注册register 验证码captchaImage 允许匿名访问
                    requests.pathMatchers("/login", "/register", "/captchaImage").permitAll()
                            // 静态资源，可匿名访问
                            .pathMatchers(HttpMethod.GET, "/", "/*.html", "/**/*.html", "/**/*.css", "/**/*.js", "/profile/**").permitAll()
                            .pathMatchers("/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/*/api-docs", "/druid/**").permitAll()
                            // 除上面外的所有请求全部需要鉴权认证
                            .anyExchange().authenticated();
                })
                // 添加Logout filter
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler))
                // 添加JWT filter
                .addFilterAt(authenticationTokenFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                // 添加CORS filter
                .addFilterAt(corsFilter, SecurityWebFiltersOrder.CORS)
                .build();
    }

    /**
     * 强散列哈希加密实现
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
