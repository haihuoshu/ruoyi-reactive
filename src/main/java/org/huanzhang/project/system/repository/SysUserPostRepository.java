package org.huanzhang.project.system.repository;

import reactor.core.publisher.Mono;

/**
 * 用户与岗位关联表 数据层
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
public interface SysUserPostRepository {

    /**
     * 根据岗位ID查询总数
     */
    Mono<Long> selectCountByPostId(Long postId);

}
