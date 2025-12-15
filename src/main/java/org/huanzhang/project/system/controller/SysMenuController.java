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
import org.huanzhang.project.system.dto.SysMenuInsertDTO;
import org.huanzhang.project.system.dto.SysMenuUpdateDTO;
import org.huanzhang.project.system.query.SysMenuQuery;
import org.huanzhang.project.system.service.SysMenuService;
import org.huanzhang.project.system.vo.SysMenuVO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "菜单管理")
@Validated
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController extends BaseController {

    private final SysMenuService sysMenuService;

    @Operation(summary = "根据条件查询菜单列表")
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/list")
    public Mono<ListResponse<SysMenuVO>> list(@ParameterObject SysMenuQuery query) {
        return sysMenuService.selectMenuList(query)
                .collectList()
                .map(ListResponse::getInstance);
    }

    @Operation(summary = "查询菜单下拉树列表")
    @GetMapping("/treeselect")
    public Mono<ListResponse<SysMenuVO>> treeselect(@ParameterObject SysMenuQuery query) {
        return sysMenuService.selectMenuList(query)
                .collectList()
                .map(TreeUtils::getTree)
                .map(ListResponse::getInstance);
    }

    @Operation(summary = "根据菜单ID查询详细信息")
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public Mono<AjaxResponse<SysMenuVO>> getInfo(@PathVariable Long menuId) {
        return sysMenuService.selectMenuById(menuId)
                .map(AjaxResponse::ok);
    }

    @Operation(summary = "新增菜单")
    @PreAuthorize("hasAuthority('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Mono<AjaxResponse<Void>> add(@RequestBody @Valid SysMenuInsertDTO dto) {
        return sysMenuService.insertMenu(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "修改菜单")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PutMapping
    public Mono<AjaxResponse<Void>> edit(@RequestBody @Valid SysMenuUpdateDTO dto) {
        return sysMenuService.updateMenu(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "删除菜单")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:menu:remove')")
    @DeleteMapping("/{menuId}")
    public Mono<AjaxResponse<Void>> remove(@PathVariable Long menuId) {
        return sysMenuService.deleteMenuById(menuId)
                .thenReturn(AjaxResponse.ok());
    }

}