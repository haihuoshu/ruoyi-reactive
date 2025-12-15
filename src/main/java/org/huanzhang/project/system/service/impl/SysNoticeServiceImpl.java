package org.huanzhang.project.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.huanzhang.common.exception.ServiceException;
import org.huanzhang.project.system.converter.SysNoticeMapper;
import org.huanzhang.project.system.dto.SysNoticeInsertDTO;
import org.huanzhang.project.system.dto.SysNoticeUpdateDTO;
import org.huanzhang.project.system.entity.SysNotice;
import org.huanzhang.project.system.query.SysNoticeQuery;
import org.huanzhang.project.system.repository.SysNoticeRepository;
import org.huanzhang.project.system.service.SysNoticeService;
import org.huanzhang.project.system.vo.SysNoticeVO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 通告表 业务处理
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl implements SysNoticeService {

    private final SysNoticeRepository sysNoticeRepository;
    private final SysNoticeMapper sysNoticeMapper;

    /**
     * 根据条件查询通告总数
     */
    @Override
    public Mono<Long> selectNoticeCountByQuery(SysNoticeQuery query) {
        return sysNoticeRepository.selectCountByQuery(query);
    }

    /**
     * 根据条件查询通告列表
     */
    @Override
    public Flux<SysNoticeVO> selectNoticeListByQuery(SysNoticeQuery query) {
        return sysNoticeRepository.selectListByQuery(query)
                .map(sysNoticeMapper::toVo);
    }

    /**
     * 根据通告ID查询详细信息
     */
    @Override
    public Mono<SysNoticeVO> selectNoticeById(Long noticeId) {
        return sysNoticeRepository.selectOneById(noticeId)
                .switchIfEmpty(ServiceException.monoInstance("通告不存在"))
                .map(sysNoticeMapper::toVo);
    }

    /**
     * 新增通告
     */
    @Override
    public Mono<Void> insertNotice(SysNoticeInsertDTO dto) {
        SysNotice entity = sysNoticeMapper.toEntity(dto);

        return sysNoticeRepository.insert(entity)
                .then();
    }

    /**
     * 修改通告
     */
    @Override
    public Mono<Void> updateNotice(SysNoticeUpdateDTO dto) {
        SysNotice entity = sysNoticeMapper.toEntity(dto);

        return sysNoticeRepository.selectOneById(dto.getNoticeId())
                .switchIfEmpty(ServiceException.monoInstance("通告不存在"))
                .then(sysNoticeRepository.updateById(entity))
                .then();
    }

    /**
     * 批量删除通告
     */
    @Override
    public Mono<Void> deleteNoticeByIds(List<Long> noticeIds) {
        return sysNoticeRepository.deleteByIds(noticeIds)
                .then();
    }

}
