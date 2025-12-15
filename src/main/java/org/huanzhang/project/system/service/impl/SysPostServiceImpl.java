package org.huanzhang.project.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.huanzhang.common.exception.ServiceException;
import org.huanzhang.project.system.converter.SysPostMapper;
import org.huanzhang.project.system.dto.SysPostInsertDTO;
import org.huanzhang.project.system.dto.SysPostUpdateDTO;
import org.huanzhang.project.system.entity.SysPost;
import org.huanzhang.project.system.query.SysPostQuery;
import org.huanzhang.project.system.repository.SysPostRepository;
import org.huanzhang.project.system.repository.SysUserPostRepository;
import org.huanzhang.project.system.service.SysPostService;
import org.huanzhang.project.system.vo.SysPostVO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 岗位表 业务处理
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Service
@RequiredArgsConstructor
public class SysPostServiceImpl implements SysPostService {

    private final SysPostRepository sysPostRepository;
    private final SysPostMapper sysPostMapper;

    private final SysUserPostRepository sysUserPostRepository;

    /**
     * 根据条件查询岗位总数
     */
    @Override
    public Mono<Long> selectPostCountByQuery(SysPostQuery query) {
        return sysPostRepository.selectCountByQuery(query);
    }

    /**
     * 根据条件查询岗位列表
     */
    @Override
    public Flux<SysPostVO> selectPostListByQuery(SysPostQuery query) {
        return sysPostRepository.selectListByQuery(query)
                .map(sysPostMapper::toVo);
    }

    /**
     * 根据岗位ID查询详细信息
     */
    @Override
    public Mono<SysPostVO> selectPostById(Long postId) {
        return sysPostRepository.selectOneById(postId)
                .switchIfEmpty(ServiceException.monoInstance("岗位不存在"))
                .map(sysPostMapper::toVo);
    }

    /**
     * 新增岗位
     */
    @Override
    public Mono<Void> insertPost(SysPostInsertDTO dto) {
        SysPost entity = sysPostMapper.toEntity(dto);

        return checkPostNameUnique(entity)
                .then(checkPostCodeUnique(entity))
                .then(sysPostRepository.insert(entity))
                .then();
    }

    /**
     * 检查岗位名称是否唯一
     */
    public Mono<Void> checkPostNameUnique(SysPost post) {
        return sysPostRepository.selectOneByPostName(post.getPostName())
                .flatMap(info -> {
                    if (ObjectUtils.notEqual(info.getPostId(), post.getPostId())) {
                        return ServiceException.monoInstance("岗位名称已存在");
                    }
                    return Mono.empty();
                })
                .then();
    }

    /**
     * 检查岗位编码是否唯一
     */
    public Mono<Void> checkPostCodeUnique(SysPost post) {
        return sysPostRepository.selectOneByPostCode(post.getPostCode())
                .flatMap(info -> {
                    if (ObjectUtils.notEqual(info.getPostId(), post.getPostId())) {
                        return ServiceException.monoInstance("岗位编码已存在");
                    }
                    return Mono.empty();
                })
                .then();
    }

    /**
     * 修改岗位
     */
    @Override
    public Mono<Void> updatePost(SysPostUpdateDTO dto) {
        SysPost entity = sysPostMapper.toEntity(dto);

        return checkPostNameUnique(entity)
                .then(checkPostCodeUnique(entity))
                .then(sysPostRepository.updateById(entity))
                .then();
    }

    /**
     * 批量删除岗位
     */
    @Override
    public Mono<Void> deletePostByIds(List<Long> postIds) {
        return sysPostRepository.selectListByIds(postIds)
                .flatMap(this::checkPostAllocated)
                .then(sysPostRepository.deleteByIds(postIds))
                .then();
    }

    /**
     * 检查岗位是否已分配
     */
    private Mono<Void> checkPostAllocated(SysPost post) {
        return sysUserPostRepository.selectCountByPostId(post.getPostId())
                .flatMap(count -> {
                    if (count > 0) {
                        return ServiceException.monoInstance(String.format("%1$s已分配，不能删除", post.getPostName()));
                    }
                    return Mono.empty();
                });
    }

    /**
     * 查询所有岗位
     */
    @Override
    public Flux<SysPostVO> selectPostAll() {
        return sysPostRepository.selectListByQuery(new SysPostQuery())
                .map(sysPostMapper::toVo);
    }
}
