package org.huanzhang.project.system.repository.impl;

import com.querydsl.r2dbc.R2DBCQueryFactory;
import lombok.RequiredArgsConstructor;
import org.huanzhang.project.system.entity.SysUserPost;
import org.huanzhang.project.system.entity.impl.QSysUserPost;
import org.huanzhang.project.system.repository.SysUserPostRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 用户与岗位关联表 数据处理
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Repository
@RequiredArgsConstructor
public class SysUserPostRepositoryImpl implements SysUserPostRepository {

    private static final QSysUserPost tb_1_ = new QSysUserPost("tb_1_");

    private final R2DBCQueryFactory queryFactory;

    /**
     * 根据岗位ID查询总数
     */
    @Override
    public Mono<Long> selectCountByPostId(Long postId) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.postId.eq(postId))
                .fetchCount();
    }

    /**
     * 根据用户ID删除
     */
    @Override
    public Mono<Long> deleteByUserIds(List<Long> userIds) {
        return queryFactory.delete(tb_1_)
                .where(tb_1_.userId.in(userIds))
                .execute();
    }

    /**
     * 根据用户ID删除
     */
    @Override
    public Mono<Long> deleteByUserId(Long userId) {
        return queryFactory.delete(tb_1_)
                .where(tb_1_.userId.eq(userId))
                .execute();
    }

    /**
     * 新增用户与岗位关联
     */
    @Override
    public Mono<Long> insert(SysUserPost userPost) {
        return queryFactory.insert(tb_1_)
                .populate(userPost)
                .execute();
    }

}
