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
import org.huanzhang.project.system.dto.SysPostInsertDTO;
import org.huanzhang.project.system.dto.SysPostUpdateDTO;
import org.huanzhang.project.system.query.SysPostQuery;
import org.huanzhang.project.system.service.SysPostService;
import org.huanzhang.project.system.vo.SysPostVO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "岗位管理")
@Validated
@RestController
@RequestMapping("/system/post")
@RequiredArgsConstructor
public class SysPostController extends BaseController {

    private final SysPostService sysPostService;

    @Operation(summary = "根据条件分页查询岗位列表")
    @PreAuthorize("hasAuthority('system:post:list')")
    @GetMapping("/list")
    public Mono<PageResponse<SysPostVO>> list(@ParameterObject @Valid SysPostQuery query) {
        Mono<Long> count = sysPostService.selectPostCountByQuery(query);

        Mono<List<SysPostVO>> list = sysPostService.selectPostListByQuery(query).collectList();

        return Mono.zip(list, count).map(tuple -> PageResponse.getInstance(tuple.getT1(), tuple.getT2()));
    }

    @Operation(summary = "根据条件导出岗位列表")
    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('system:post:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @ParameterObject SysPostQuery query) {
        sysPostService.selectPostListByQuery(query)
                .collectList()
                .subscribe(list -> {
                    ExcelUtil<SysPostVO> util = new ExcelUtil<>(SysPostVO.class);
                    util.exportExcel(response, list, "岗位数据");
                });
    }

    @Operation(summary = "根据岗位ID查询详细信息")
    @PreAuthorize("hasAuthority('system:post:query')")
    @GetMapping(value = "/{postId}")
    public Mono<AjaxResponse<SysPostVO>> getInfo(@PathVariable Long postId) {
        return sysPostService.selectPostById(postId)
                .map(AjaxResponse::ok);
    }

    @Operation(summary = "新增岗位")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:post:add')")
    @PostMapping
    public Mono<AjaxResponse<Void>> add(@RequestBody @Valid SysPostInsertDTO dto) {
        return sysPostService.insertPost(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "修改岗位")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:post:edit')")
    @PutMapping
    public Mono<AjaxResponse<Void>> edit(@RequestBody @Valid SysPostUpdateDTO dto) {
        return sysPostService.updatePost(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "批量删除岗位")
    @PreAuthorize("hasAuthority('system:post:remove')")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public Mono<AjaxResponse<Void>> remove(@PathVariable List<Long> postIds) {
        return sysPostService.deletePostByIds(postIds)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "查询所有岗位")
    @GetMapping("/optionselect")
    public Mono<ListResponse<SysPostVO>> optionselect() {
        return sysPostService.selectPostAll()
                .collectList()
                .map(ListResponse::getInstance);
    }

}
