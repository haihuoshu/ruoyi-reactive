package org.huanzhang.project.system.controller;

import jakarta.annotation.Resource;
import org.huanzhang.common.exception.ServiceException;
import org.huanzhang.framework.security.RegisterBody;
import org.huanzhang.framework.security.service.SysRegisterService;
import org.huanzhang.framework.web.controller.BaseController;
import org.huanzhang.framework.web.domain.AjaxResponse;
import org.huanzhang.project.system.service.SysConfigService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 注册验证
 *
 * @author ruoyi
 */
@RestController
public class SysRegisterController extends BaseController {

    @Resource
    private SysRegisterService registerService;

    @Resource
    private SysConfigService configService;

    @PostMapping("/register")
    public Mono<AjaxResponse<Void>> register(@RequestBody RegisterBody user) {
        return configService.selectConfigByKey("sys.account.registerUser")
                .flatMap(registerUser -> {
                    if (!("true".equals(registerUser))) {
                        return ServiceException.monoInstance("当前系统没有开启注册功能！");
                    }
                    return registerService.register(user)
                            .map(msg -> {
                                if (org.apache.commons.lang3.StringUtils.isNotBlank(msg)) {
                                    return AjaxResponse.fail(msg);
                                } else {
                                    return AjaxResponse.ok();
                                }
                            });
                });
    }
}
