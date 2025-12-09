package org.huanzhang.project.system.controller;

import jakarta.annotation.Resource;
import org.huanzhang.common.constant.Constants;
import org.huanzhang.common.core.text.Convert;
import org.huanzhang.common.utils.DateUtils;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.framework.security.LoginBody;
import org.huanzhang.framework.security.LoginUser;
import org.huanzhang.framework.security.service.SysLoginService;
import org.huanzhang.framework.security.service.SysPermissionService;
import org.huanzhang.framework.security.service.TokenService;
import org.huanzhang.framework.web.domain.AjaxResponse;
import org.huanzhang.framework.web.domain.AjaxResult;
import org.huanzhang.project.system.domain.SysMenu;
import org.huanzhang.project.system.domain.SysUser;
import org.huanzhang.project.system.domain.vo.RouterVo;
import org.huanzhang.project.system.service.ISysMenuService;
import org.huanzhang.project.system.service.SysConfigService;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 登录验证
 *
 * @author ruoyi
 */
@RestController
public class SysLoginController {
    @Resource
    private SysLoginService loginService;

    @Resource
    private ISysMenuService menuService;

    @Resource
    private SysPermissionService permissionService;

    @Resource
    private TokenService tokenService;

    @Resource
    private SysConfigService configService;

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody, ServerHttpRequest request) {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(request, loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public Mono<AjaxResult> getInfo() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(LoginUser.class)
                .map(loginUser -> {
                    SysUser user = loginUser.getUser();
                    // 角色集合
                    Set<String> roles = permissionService.getRolePermission(user);
                    // 权限集合
                    Set<String> permissions = permissionService.getMenuPermission(user);
                    if (!loginUser.getPermissions().equals(permissions)) {
                        loginUser.setPermissions(permissions);
                        tokenService.refreshToken(loginUser);
                    }
                    AjaxResult ajax = AjaxResult.success();
                    ajax.put("user", user);
                    ajax.put("roles", roles);
                    ajax.put("permissions", permissions);
                    ajax.put("isDefaultModifyPwd", initPasswordIsModify(user.getPwdUpdateDate()));
                    ajax.put("isPasswordExpired", passwordIsExpiration(user.getPwdUpdateDate()));
                    return ajax;
                });
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public Mono<AjaxResponse<List<RouterVo>>> getRouters() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(LoginUser.class)
                .map(LoginUser::getUserId)
                .map(userId -> {
                    List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
                    return AjaxResponse.ok(menuService.buildMenus(menus));
                });
    }

    // 检查初始密码是否提醒修改
    public boolean initPasswordIsModify(Date pwdUpdateDate) {
        //noinspection ReactiveStreamsUnusedPublisher
        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    // 检查密码是否过期
    public boolean passwordIsExpiration(Date pwdUpdateDate) {
        //noinspection ReactiveStreamsUnusedPublisher
        Integer passwordValidateDays = Convert.toInt(configService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0) {
            if (StringUtils.isNull(pwdUpdateDate)) {
                // 如果从未修改过初始密码，直接提醒过期
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }
}
