package org.huanzhang.project.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.huanzhang.framework.aspectj.lang.annotation.Log;
import org.huanzhang.framework.aspectj.lang.enums.BusinessType;
import org.huanzhang.framework.web.controller.BaseController;
import org.huanzhang.framework.web.domain.AjaxResponse;
import org.huanzhang.framework.web.domain.ListResponse;
import org.huanzhang.framework.web.tree.TreeUtils;
import org.huanzhang.project.system.dto.SysDeptInsertDTO;
import org.huanzhang.project.system.dto.SysDeptUpdateDTO;
import org.huanzhang.project.system.query.SysDeptQuery;
import org.huanzhang.project.system.service.SysDeptService;
import org.huanzhang.project.system.vo.SysDeptVO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "部门管理")
@Validated
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class SysDeptController extends BaseController {

    private final SysDeptService deptService;

    @Operation(summary = "根据条件查询部门列表")
    @PreAuthorize("hasAuthority('system:dept:list')")
    @GetMapping("/list")
    public Mono<ListResponse<SysDeptVO>> list(@ParameterObject SysDeptQuery query) {
        return deptService.selectDeptList(query)
                .collectList()
                .map(ListResponse::getInstance);
    }

    @Operation(summary = "查询部门树列表")
    @GetMapping("/deptTree")
    public Mono<ListResponse<SysDeptVO>> deptTree(@ParameterObject SysDeptQuery query) {
        return deptService.selectDeptList(query)
                .collectList()
                .map(TreeUtils::getTree)
                .map(ListResponse::getInstance);
    }

    @Operation(summary = "查询部门列表（排除节点）")
    @PreAuthorize("hasAuthority('system:dept:list')")
    @GetMapping("/list/exclude/{deptId}")
    public Mono<ListResponse<SysDeptVO>> excludeChild(@PathVariable(required = false) Long deptId) {
        return deptService.selectDeptListExclude(deptId)
                .collectList()
                .map(ListResponse::getInstance);
    }

    @Operation(summary = "根据部门ID查询详细信息")
    @PreAuthorize("hasAuthority('system:dept:query')")
    @GetMapping(value = "/{deptId}")
    public Mono<AjaxResponse<SysDeptVO>> getInfo(@PathVariable Long deptId) {
        return deptService.selectDeptById(deptId)
                .map(AjaxResponse::ok);
    }

    @Operation(summary = "新增部门")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:dept:add')")
    @PostMapping
    public Mono<AjaxResponse<Void>> add(@RequestBody @Valid SysDeptInsertDTO dto) {
        return deptService.insertDept(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "修改部门")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:dept:edit')")
    @PutMapping
    public Mono<AjaxResponse<Void>> edit(@Validated @RequestBody SysDeptUpdateDTO dto) {
        return deptService.updateDept(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "删除部门")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:dept:remove')")
    @DeleteMapping("/{deptId}")
    public Mono<AjaxResponse<Void>> remove(@PathVariable Long deptId) {
        return deptService.deleteDeptById(deptId)
                .thenReturn(AjaxResponse.ok());
    }

}
