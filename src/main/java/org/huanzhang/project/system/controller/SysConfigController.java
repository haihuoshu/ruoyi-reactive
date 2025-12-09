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
import org.huanzhang.project.system.dto.SysConfigInsertDTO;
import org.huanzhang.project.system.dto.SysConfigUpdateDTO;
import org.huanzhang.project.system.query.SysConfigQuery;
import org.huanzhang.project.system.service.SysConfigService;
import org.huanzhang.project.system.vo.SysConfigVO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "配置管理")
@Validated
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class SysConfigController extends BaseController {

    private final SysConfigService sysConfigService;

    @Operation(summary = "根据条件分页查询配置列表")
    @PreAuthorize("hasAuthority('system:config:list')")
    @GetMapping("/list")
    public Mono<PageResponse<SysConfigVO>> list(@ParameterObject @Valid SysConfigQuery query) {
        Mono<List<SysConfigVO>> list = sysConfigService.selectConfigList(query).collectList();

        Mono<Long> count = sysConfigService.selectConfigCount(query);

        return Mono.zip(list, count).map(t -> PageResponse.getInstance(t.getT1(), t.getT2()));
    }

    @Operation(summary = "根据条件导出配置列表")
    @Log(title = "配置管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('system:config:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysConfigQuery query) {
        sysConfigService.selectConfigList(query)
                .collectList()
                .subscribe(list -> {
                    ExcelUtil<SysConfigVO> util = new ExcelUtil<>(SysConfigVO.class);
                    util.exportExcel(response, list, "参数数据");
                });
    }

    @Operation(summary = "根据配置ID查询详细信息")
    @PreAuthorize("hasAuthority('system:config:query')")
    @GetMapping(value = "/{configId}")
    public Mono<AjaxResponse<SysConfigVO>> getInfo(@PathVariable Long configId) {
        return sysConfigService.selectConfigById(configId)
                .map(AjaxResponse::ok);
    }

    @Operation(summary = "根据配置键查询配置值")
    @GetMapping(value = "/configKey/{configKey}")
    public Mono<AjaxResponse<String>> getConfigKey(@PathVariable String configKey) {
        return sysConfigService.selectConfigByKey(configKey)
                .map(AjaxResponse::ok);
    }

    @Operation(summary = "新增配置")
    @Log(title = "配置管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:config:add')")
    @PostMapping
    public Mono<AjaxResponse<Void>> add(@RequestBody @Valid SysConfigInsertDTO dto) {
        return sysConfigService.insertConfig(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "修改配置")
    @Log(title = "配置管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:config:edit')")
    @PutMapping
    public Mono<AjaxResponse<Void>> edit(@RequestBody @Valid SysConfigUpdateDTO dto) {
        return sysConfigService.updateConfig(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "批量删除配置")
    @Log(title = "配置管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:config:remove')")
    @DeleteMapping("/{configIds}")
    public Mono<AjaxResponse<Void>> remove(@PathVariable List<Long> configIds) {
        return sysConfigService.deleteConfigByIds(configIds)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "刷新配置缓存")
    @Log(title = "配置管理", businessType = BusinessType.CLEAN)
    @PreAuthorize("hasAuthority('system:config:remove')")
    @DeleteMapping("/refreshCache")
    public Mono<AjaxResponse<Void>> refreshCache() {
        return sysConfigService.refreshConfigCache()
                .thenReturn(AjaxResponse.ok());
    }

}
