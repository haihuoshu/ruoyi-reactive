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
import org.huanzhang.project.system.dto.SysUserInsertDTO;
import org.huanzhang.project.system.dto.SysUserUpdateDTO;
import org.huanzhang.project.system.query.SysUserQuery;
import org.huanzhang.project.system.service.SysUserService;
import org.huanzhang.project.system.vo.SysUserVO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "用户管理")
@Validated
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController extends BaseController {

    private final SysUserService sysUserService;

    @Operation(summary = "根据条件分页查询用户列表")
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/list")
    public Mono<PageResponse<SysUserVO>> list(@ParameterObject @Valid SysUserQuery query) {
        Mono<Long> count = sysUserService.selectUserCountByQuery(query);

        Mono<List<SysUserVO>> list = sysUserService.selectUserListByQuery(query).collectList();

        return Mono.zip(list, count).map(tuple -> PageResponse.getInstance(tuple.getT1(), tuple.getT2()));
    }

    @Operation(summary = "根据条件导出用户列表")
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @ParameterObject SysUserQuery query) {
        sysUserService.selectUserListByQuery(query)
                .collectList()
                .subscribe(list -> {
                    ExcelUtil<SysUserVO> util = new ExcelUtil<>(SysUserVO.class);
                    util.exportExcel(response, list, "用户数据");
                });
    }

    @Operation(summary = "根据用户ID查询详细信息")
    @PreAuthorize("hasAuthority('system:user:query')")
    @GetMapping(value = "/{userId}")
    public Mono<AjaxResponse<SysUserVO>> getInfo(@PathVariable Long userId) {
        return sysUserService.selectUserById(userId)
                .map(AjaxResponse::ok);
    }

    @Operation(summary = "新增用户")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:user:add')")
    @PostMapping
    public Mono<AjaxResponse<Void>> add(@RequestBody @Valid SysUserInsertDTO dto) {
        return sysUserService.insertUser(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "修改用户")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping
    public Mono<AjaxResponse<Void>> edit(@RequestBody @Valid SysUserUpdateDTO dto) {
        return sysUserService.updateUser(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "重置密码")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    @PutMapping("/resetPwd")
    public Mono<AjaxResponse<Void>> resetPwd(@RequestBody @Valid SysUserUpdateDTO dto) {
        return sysUserService.updateUserPassword(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "状态修改")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping("/changeStatus")
    public Mono<AjaxResponse<Void>> changeStatus(@RequestBody @Valid SysUserUpdateDTO dto) {
        return sysUserService.updateUserStatus(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "用户授权角色")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping("/authRole")
    public Mono<AjaxResponse<Void>> insertAuthRole(@RequestBody @Valid SysUserUpdateDTO dto) {
        return sysUserService.updateUserRole(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "批量删除用户")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:user:remove')")
    @DeleteMapping("/{userIds}")
    public Mono<AjaxResponse<Void>> remove(@PathVariable List<Long> userIds) {
        return sysUserService.deleteUserByIds(userIds)
                .thenReturn(AjaxResponse.ok());
    }

}
