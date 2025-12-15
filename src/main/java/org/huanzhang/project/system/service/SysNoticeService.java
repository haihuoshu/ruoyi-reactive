package org.huanzhang.project.system.service;

import org.huanzhang.project.system.dto.SysNoticeInsertDTO;
import org.huanzhang.project.system.dto.SysNoticeUpdateDTO;
import org.huanzhang.project.system.query.SysNoticeQuery;
import org.huanzhang.project.system.vo.SysNoticeVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 通告表 业务层
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
public interface SysNoticeService {

    /**
     * 根据条件查询通告总数
     */
    Mono<Long> selectNoticeCountByQuery(SysNoticeQuery query);

    /**
     * 根据条件查询通告列表
     */
    Flux<SysNoticeVO> selectNoticeListByQuery(SysNoticeQuery query);

    /**
     * 根据通告ID查询详细信息
     */
    Mono<SysNoticeVO> selectNoticeById(Long noticeId);

    /**
     * 新增通告
     */
    Mono<Void> insertNotice(SysNoticeInsertDTO dto);

    /**
     * 修改通告
     */
    Mono<Void> updateNotice(SysNoticeUpdateDTO dto);

    /**
     * 批量删除通告
     */
    Mono<Void> deleteNoticeByIds(List<Long> noticeIds);

}
