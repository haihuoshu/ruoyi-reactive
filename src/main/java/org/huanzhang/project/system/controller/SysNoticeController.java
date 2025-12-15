package org.huanzhang.project.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.huanzhang.framework.aspectj.lang.annotation.Log;
import org.huanzhang.framework.aspectj.lang.enums.BusinessType;
import org.huanzhang.framework.web.controller.BaseController;
import org.huanzhang.framework.web.domain.AjaxResponse;
import org.huanzhang.framework.web.domain.PageResponse;
import org.huanzhang.project.system.dto.SysNoticeInsertDTO;
import org.huanzhang.project.system.dto.SysNoticeUpdateDTO;
import org.huanzhang.project.system.query.SysNoticeQuery;
import org.huanzhang.project.system.service.SysNoticeService;
import org.huanzhang.project.system.vo.SysNoticeVO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "通告管理")
@Validated
@RestController
@RequestMapping("/system/notice")
@RequiredArgsConstructor
public class SysNoticeController extends BaseController {

    private final SysNoticeService noticeService;

    @Operation(summary = "根据条件分页查询通告列表")
    @PreAuthorize("hasAuthority('system:notice:list')")
    @GetMapping("/list")
    public Mono<PageResponse<SysNoticeVO>> list(@ParameterObject @Valid SysNoticeQuery query) {
        Mono<Long> count = noticeService.selectNoticeCountByQuery(query);

        Mono<List<SysNoticeVO>> list = noticeService.selectNoticeListByQuery(query).collectList();

        return Mono.zip(list, count).map(tuple -> PageResponse.getInstance(tuple.getT1(), tuple.getT2()));
    }

    @Operation(summary = "根据通告ID查询详细信息")
    @PreAuthorize("hasAuthority('system:notice:query')")
    @GetMapping(value = "/{noticeId}")
    public Mono<AjaxResponse<SysNoticeVO>> getInfo(@PathVariable Long noticeId) {
        return noticeService.selectNoticeById(noticeId)
                .map(AjaxResponse::ok);
    }

    @Operation(summary = "新增通告")
    @Log(title = "通告管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:notice:add')")
    @PostMapping
    public Mono<AjaxResponse<Void>> add(@RequestBody @Valid SysNoticeInsertDTO dto) {
        return noticeService.insertNotice(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "修改通告")
    @Log(title = "通告管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:notice:edit')")
    @PutMapping
    public Mono<AjaxResponse<Void>> edit(@Validated @RequestBody SysNoticeUpdateDTO dto) {
        return noticeService.updateNotice(dto)
                .thenReturn(AjaxResponse.ok());
    }

    @Operation(summary = "批量删除通告")
    @Log(title = "通告管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:notice:remove')")
    @DeleteMapping("/{noticeIds}")
    public Mono<AjaxResponse<Void>> remove(@PathVariable List<Long> noticeIds) {
        return noticeService.deleteNoticeByIds(noticeIds)
                .thenReturn(AjaxResponse.ok());
    }

}
