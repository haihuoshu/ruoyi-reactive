package org.huanzhang.project.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.huanzhang.common.utils.poi.ExcelUtil;
import org.huanzhang.framework.aspectj.lang.annotation.Log;
import org.huanzhang.framework.aspectj.lang.enums.BusinessType;
import org.huanzhang.framework.web.controller.BaseController;
import org.huanzhang.framework.web.domain.AjaxResponse;
import org.huanzhang.framework.web.domain.AjaxResult;
import org.huanzhang.framework.web.domain.ListResponse;
import org.huanzhang.framework.web.domain.PageResponse;
import org.huanzhang.framework.web.page.TableDataInfo;
import org.huanzhang.project.system.dto.SysRoleInsertDTO;
import org.huanzhang.project.system.dto.SysRoleUpdateDTO;
import org.huanzhang.project.system.dto.SysUserInsertDTO;
import org.huanzhang.project.system.entity.SysUserRole;
import org.huanzhang.project.system.query.SysRoleQuery;
import org.huanzhang.project.system.service.SysRoleService;
import org.huanzhang.project.system.service.SysUserService;
import org.huanzhang.project.system.vo.SysRoleVO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Tag(name = "角色管理")
@Validated
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController extends BaseController {

    private final SysRoleService sysRoleService;

    @Resource
    private SysUserService userService;

    @Operation(summary = "获取角色选择框列表")
    @GetMapping("/optionselect")
    public Mono<ListResponse<SysRoleVO>> optionselect(@ParameterObject SysRoleQuery query) {
        return sysRoleService.selectRoleListByQuery(query)
                .collectList()
                .map(ListResponse::getInstance);
    }

    @Operation(summary = "根据条件分页查询岗位列表")
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/list")
    public Mono<PageResponse<SysRoleVO>> list(@ParameterObject @Valid SysRoleQuery query) {
        Mono<Long> count = sysRoleService.selectRoleCountByQuery(query);

        Mono<List<SysRoleVO>> list = sysRoleService.selectRoleListByQuery(query).collectList();

        return Mono.zip(list, count).map(tuple -> PageResponse.getInstance(tuple.getT1(), tuple.getT2()));
    }

    @Operation(summary = "根据条件导出岗位列表")
    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('system:role:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @ParameterObject SysRoleQuery query) {
        sysRoleService.selectRoleListByQuery(query)
                .collectList()
                .subscribe(list -> {
                    ExcelUtil<SysRoleVO> util = new ExcelUtil<>(SysRoleVO.class);
                    util.exportExcel(response, list, "角色数据");
                });
    }

    @Operation(summary = "根据角色ID查询详细信息")
    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public Mono<AjaxResponse<SysRoleVO>> getInfo(@PathVariable Long roleId) {
        return sysRoleService.selectRoleById(roleId)
                .map(AjaxResponse::ok);
    }

    @Operation(summary = "新增角色")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:role:add')")
    @PostMapping
    public Mono<AjaxResponse<Void>> add(@RequestBody @Valid SysRoleInsertDTO dto) {
        return sysRoleService.insertRole(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "修改角色")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping
    public Mono<AjaxResponse<Void>> edit(@RequestBody @Valid SysRoleUpdateDTO dto) {
        return sysRoleService.updateRole(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "修改角色状态")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping("/changeStatus")
    public Mono<AjaxResponse<Void>> changeStatus(@RequestBody @Valid SysRoleUpdateDTO dto) {
        return sysRoleService.updateRoleStatus(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "修改数据权限")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping("/dataScope")
    public Mono<AjaxResponse<Void>> dataScope(@RequestBody @Valid SysRoleUpdateDTO dto) {
        return sysRoleService.updateDataScope(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "批量删除角色")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:role:remove')")
    @DeleteMapping("/{roleIds}")
    public Mono<AjaxResponse<Void>> remove(@PathVariable List<Long> roleIds) {
        return sysRoleService.deleteRoleByIds(roleIds)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "查询已分配用户角色列表")
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public TableDataInfo allocatedList(SysUserInsertDTO user) {
        startPage();
        List<SysUserInsertDTO> list = userService.selectAllocatedList(user);
        return getDataTable(list);
    }

    @Operation(summary = "查询未分配用户角色列表")
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public TableDataInfo unallocatedList(SysUserInsertDTO user) {
        startPage();
        List<SysUserInsertDTO> list = userService.selectUnallocatedList(user);
        return getDataTable(list);
    }

    @Operation(summary = "取消授权用户")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public AjaxResult cancelAuthUser(@RequestBody SysUserRole userRole) {
        return toAjax(sysRoleService.deleteAuthUser(userRole));
    }

    @Operation(summary = "批量取消授权用户")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public AjaxResult cancelAuthUserAll(Long roleId, Long[] userIds) {
        return toAjax(sysRoleService.deleteAuthUsers(roleId, userIds));
    }

    @Operation(summary = "批量选择用户授权")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public AjaxResult selectAuthUserAll(Long roleId, Long[] userIds) {
        sysRoleService.checkRoleDataScope(Collections.singletonList(roleId));
        return toAjax(sysRoleService.insertAuthUsers(roleId, userIds));
    }

}
