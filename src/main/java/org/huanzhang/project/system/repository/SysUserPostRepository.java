package org.huanzhang.project.system.repository;

import org.huanzhang.project.system.entity.SysUserPost;
import reactor.core.publisher.Mono;

import java.util.List;

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

    /**
     * 根据用户ID删除
     */
    Mono<Long> deleteByUserIds(List<Long> userIds);

    /**
     * 根据用户ID删除
     */
    Mono<Long> deleteByUserId(Long userId);

    /**
     * 新增用户与岗位关联
     */
    Mono<Long> insert(SysUserPost userPost);

}
