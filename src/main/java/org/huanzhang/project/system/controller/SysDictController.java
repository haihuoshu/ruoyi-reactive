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
import org.huanzhang.framework.web.domain.ListResponse;
import org.huanzhang.framework.web.domain.PageResponse;
import org.huanzhang.project.system.dto.SysDictInsertDTO;
import org.huanzhang.project.system.dto.SysDictUpdateDTO;
import org.huanzhang.project.system.query.SysDictQuery;
import org.huanzhang.project.system.service.SysDictService;
import org.huanzhang.project.system.vo.SysDictVO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "字典管理")
@Validated
@RestController
@RequestMapping("/system/dict")
@RequiredArgsConstructor
public class SysDictController extends BaseController {

    private final SysDictService sysDictService;

    @Operation(summary = "根据条件分页查询字典列表")
    @PreAuthorize("hasAuthority('system:dict:list')")
    @GetMapping("/list")
    public Mono<PageResponse<SysDictVO>> list(@ParameterObject @Valid SysDictQuery query) {
        Mono<Long> count = sysDictService.selectDictCountByQuery(query);

        Mono<List<SysDictVO>> list = sysDictService.selectDictListByQuery(query).collectList();

        return Mono.zip(list, count).map(t -> PageResponse.getInstance(t.getT1(), t.getT2()));
    }

    @Operation(summary = "根据条件导出字典列表")
    @Log(title = "字典管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('system:dict:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictQuery query) {
        sysDictService.selectDictListByQuery(query)
                .collectList()
                .subscribe(list -> {
                    ExcelUtil<SysDictVO> util = new ExcelUtil<>(SysDictVO.class);
                    util.exportExcel(response, list, "字典管理");
                });
    }

    @Operation(summary = "根据字典类型查询字典列表")
    @GetMapping(value = "/type/{dictType}")
    public Mono<ListResponse<SysDictVO>> listByType(@PathVariable String dictType) {
        return sysDictService.selectDictListByType(dictType)
                .collectList()
                .map(ListResponse::getInstance);
    }

    @Operation(summary = "根据字典ID查询详细信息")
    @PreAuthorize("hasAuthority('system:dict:query')")
    @GetMapping(value = "/{dictId}")
    public Mono<AjaxResponse<SysDictVO>> getInfo(@PathVariable Long dictId) {
        return sysDictService.selectDictById(dictId)
                .map(AjaxResponse::ok);
    }

    @Operation(summary = "新增字典")
    @Log(title = "字典管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:dict:add')")
    @PostMapping
    public Mono<AjaxResponse<Void>> add(@RequestBody @Valid SysDictInsertDTO dto) {
        return sysDictService.insertDict(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "修改字典")
    @Log(title = "字典管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:dict:edit')")
    @PutMapping
    public Mono<AjaxResponse<Void>> edit(@RequestBody @Valid SysDictUpdateDTO dto) {
        return sysDictService.updateDict(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "批量删除字典")
    @Log(title = "字典管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:dict:remove')")
    @DeleteMapping("/{dictIds}")
    public Mono<AjaxResponse<Void>> remove(@PathVariable List<Long> dictIds) {
        return sysDictService.deleteDictByIds(dictIds)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "刷新字典缓存")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public Mono<AjaxResponse<Void>> refreshCache() {
        return sysDictService.refreshDictCache()
                .thenReturn(AjaxResponse.ok());
    }

}
