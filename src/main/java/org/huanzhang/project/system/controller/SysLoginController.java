package org.huanzhang.project.system.controller;

import jakarta.annotation.Resource;
import org.huanzhang.common.constant.Constants;
import org.huanzhang.common.core.text.Convert;
import org.huanzhang.common.utils.DateUtils;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.framework.security.LoginBody;
import org.huanzhang.framework.security.ReactiveSecurityUtils;
import org.huanzhang.framework.security.service.SysLoginService;
import org.huanzhang.framework.security.service.TokenService;
import org.huanzhang.framework.web.domain.AjaxResponse;
import org.huanzhang.framework.web.domain.AjaxResult;
import org.huanzhang.project.system.domain.vo.RouterVo;
import org.huanzhang.project.system.entity.SysUser;
import org.huanzhang.project.system.service.SysConfigService;
import org.huanzhang.project.system.service.SysMenuService;
import org.huanzhang.project.system.service.SysRoleService;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
    private SysMenuService menuService;

    @Resource
    private SysRoleService sysRoleService;

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
    public Mono<AjaxResult> login(@RequestBody LoginBody loginBody, ServerHttpRequest request) {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        return loginService.login(request, loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(), loginBody.getUuid())
                .map(token -> {
                    ajax.put(Constants.TOKEN, token);
                    return ajax;
                });
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public Mono<AjaxResult> getInfo() {
        return ReactiveSecurityUtils.getLoginUser()
                .flatMap(loginUser -> {
                    SysUser user = loginUser.getUser();
                    // 角色集合
                    Mono<List<String>> roles = sysRoleService.selectRolePermissionByUserId(loginUser.getUserId()).collectList();
                    // 权限集合
                    Mono<Set<String>> setMono = menuService.selectMenuPermsByUserId(loginUser.getUserId());

                    return Mono.zip(roles, setMono)
                            .map(tuple -> {
                                if (!loginUser.getPermissions().equals(tuple.getT2())) {
                                    loginUser.setPermissions(tuple.getT2());
                                    tokenService.refreshToken(loginUser);
                                }
                                AjaxResult ajax = AjaxResult.success();
                                ajax.put("user", user);
                                ajax.put("roles", tuple.getT1());
                                ajax.put("permissions", tuple.getT2());
                                ajax.put("isDefaultModifyPwd", initPasswordIsModify(user.getPwdUpdateDate()));
                                ajax.put("isPasswordExpired", passwordIsExpiration(user.getPwdUpdateDate()));
                                return ajax;
                            });
                });
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public Mono<AjaxResponse<List<RouterVo>>> getRouters() {
        return ReactiveSecurityUtils.getUserId()
                .flatMap(userId -> {
                    // 根据用户ID查询菜单树信息
                    return menuService.selectMenuTreeByUserId(userId)
                            .map(menuService::buildMenus)
                            .map(AjaxResponse::ok);
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
