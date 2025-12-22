package org.huanzhang.project.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.huanzhang.common.utils.poi.ExcelUtil;
import org.huanzhang.framework.aspectj.lang.annotation.Log;
import org.huanzhang.framework.aspectj.lang.enums.BusinessType;
import org.huanzhang.framework.security.service.SysPasswordService;
import org.huanzhang.framework.web.controller.BaseController;
import org.huanzhang.framework.web.domain.AjaxResponse;
import org.huanzhang.framework.web.domain.PageResponse;
import org.huanzhang.project.system.query.SysAccessLogQuery;
import org.huanzhang.project.system.service.SysAccessLogService;
import org.huanzhang.project.system.vo.SysAccessLogVO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "访问日志")
@Validated
@RestController
@RequestMapping("/monitor/logininfor")
@RequiredArgsConstructor
public class SysAccessLogController extends BaseController {

    private final SysAccessLogService sysAccessLogService;

    private final SysPasswordService passwordService;

    @Operation(summary = "根据条件分页查询访问日志")
    @PreAuthorize("hasAuthority('monitor:logininfor:list')")
    @GetMapping("/list")
    public Mono<PageResponse<SysAccessLogVO>> list(@ParameterObject @Valid SysAccessLogQuery query) {
        Mono<List<SysAccessLogVO>> list = sysAccessLogService.selectAccessLogList(query).collectList();

        Mono<Long> count = sysAccessLogService.selectAccessLogCount(query);

        return Mono.zip(list, count).map(t -> PageResponse.getInstance(t.getT1(), t.getT2()));
    }

    @Operation(summary = "根据条件导出访问日志")
    @Log(title = "访问日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('monitor:logininfor:export')")
    @PostMapping("/export")
    public void export(ServerHttpResponse response, @ParameterObject SysAccessLogQuery query) {
        sysAccessLogService.selectAccessLogList(query)
                .collectList()
                .subscribe(list -> {
                    ExcelUtil<SysAccessLogVO> util = new ExcelUtil<>(SysAccessLogVO.class);
                    util.exportExcel(response, list, "访问日志");
                });
    }

    @Operation(summary = "批量删除访问日志")
    @Log(title = "访问日志", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('monitor:logininfor:remove')")
    @DeleteMapping("/{infoIds}")
    public Mono<AjaxResponse<Void>> remove(@PathVariable List<Long> infoIds) {
        return sysAccessLogService.deleteAccessLogByIds(infoIds)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "清空访问日志")
    @Log(title = "访问日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("hasAuthority('monitor:logininfor:remove')")
    @DeleteMapping("/clean")
    public Mono<AjaxResponse<Void>> clean() {
        return sysAccessLogService.cleanAccessLog()
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "根据用户账户解锁")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @PreAuthorize("hasAuthority('monitor:logininfor:unlock')")
    @GetMapping("/unlock/{userName}")
    public Mono<AjaxResponse<Void>> unlock(@PathVariable String userName) {
        return passwordService.clearLoginRecordCache(userName)
                .thenReturn(AjaxResponse.ok());
    }
}
