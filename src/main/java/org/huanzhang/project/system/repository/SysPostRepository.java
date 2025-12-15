package org.huanzhang.project.system.repository;

import org.huanzhang.framework.r2dbc.repository.AuditableRepository;
import org.huanzhang.project.system.entity.SysPost;
import org.huanzhang.project.system.query.SysPostQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 岗位表 数据层
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
public interface SysPostRepository extends AuditableRepository<SysPost> {

    /**
     * 根据条件查询总数
     */
    Mono<Long> selectCountByQuery(SysPostQuery query);

    /**
     * 根据条件查询列表
     */
    Flux<SysPost> selectListByQuery(SysPostQuery query);

    /**
     * 根据主键查询
     */
    Mono<SysPost> selectOneById(Long postId);

    /**
     * 根据岗位名称查询
     */
    Mono<SysPost> selectOneByPostName(String postName);

    /**
     * 根据岗位编码查询
     */
    Mono<SysPost> selectOneByPostCode(String postCode);

    /**
     * 新增
     */
    Mono<Long> insert(SysPost post);

    /**
     * 根据主键修改
     */
    Mono<Long> updateById(SysPost post);

    /**
     * 根据主键查询
     */
    Flux<SysPost> selectListByIds(List<Long> postIds);

    /**
     * 根据主键批量删除
     */
    Mono<Long> deleteByIds(List<Long> postIds);

}
