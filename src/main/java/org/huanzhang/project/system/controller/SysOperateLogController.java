package org.huanzhang.project.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.huanzhang.common.utils.poi.ExcelUtil;
import org.huanzhang.framework.aspectj.lang.annotation.Log;
import org.huanzhang.framework.aspectj.lang.enums.BusinessType;
import org.huanzhang.framework.web.controller.BaseController;
import org.huanzhang.framework.web.domain.AjaxResponse;
import org.huanzhang.framework.web.domain.PageResponse;
import org.huanzhang.project.system.query.SysOperateLogQuery;
import org.huanzhang.project.system.service.SysOperateLogService;
import org.huanzhang.project.system.vo.SysOperateLogVO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "操作日志")
@Validated
@RestController
@RequestMapping("/monitor/operlog")
@RequiredArgsConstructor
public class SysOperateLogController extends BaseController {

    private final SysOperateLogService sysOperateLogService;

    @Operation(summary = "根据条件分页查询操作日志")
    @PreAuthorize("hasAuthority('monitor:operlog:list')")
    @GetMapping("/list")
    public Mono<PageResponse<SysOperateLogVO>> list(@ParameterObject @Valid SysOperateLogQuery query) {
        Mono<List<SysOperateLogVO>> list = sysOperateLogService.selectOperateLogList(query).collectList();

        Mono<Long> count = sysOperateLogService.selectOperateLogCount(query);

        return Mono.zip(list, count).map(t -> PageResponse.getInstance(t.getT1(), t.getT2()));
    }

    @Operation(summary = "根据条件导出操作日志")
    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('monitor:operlog:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @ParameterObject SysOperateLogQuery query) {
        sysOperateLogService.selectOperateLogList(query)
                .collectList()
                .subscribe(list -> {
                    ExcelUtil<SysOperateLogVO> util = new ExcelUtil<>(SysOperateLogVO.class);
                    util.exportExcel(response, list, "操作日志");
                });
    }

    @Operation(summary = "批量删除操作日志")
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public Mono<AjaxResponse<Void>> remove(@PathVariable List<Long> operIds) {
        return sysOperateLogService.deleteOperateLogByIds(operIds)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "清空操作日志")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("hasAuthority('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public Mono<AjaxResponse<Void>> clean() {
        return sysOperateLogService.cleanOperLog()
                .thenReturn(AjaxResponse.ok());
    }
}
