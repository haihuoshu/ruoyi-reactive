package org.huanzhang.project.system.repository.impl;

import com.querydsl.r2dbc.R2DBCQueryFactory;
import lombok.RequiredArgsConstructor;
import org.huanzhang.project.system.entity.SysUserRole;
import org.huanzhang.project.system.entity.impl.QSysUserRole;
import org.huanzhang.project.system.repository.SysUserRoleRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 用户与角色关联表 数据处理
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Repository
@RequiredArgsConstructor
public class SysUserRoleRepositoryImpl implements SysUserRoleRepository {

    private static final QSysUserRole tb_1_ = new QSysUserRole("tb_1_");

    private final R2DBCQueryFactory queryFactory;

    /**
     * 通过角色ID查询总数
     */
    @Override
    public Mono<Long> selectCountByRoleId(Long roleId) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.roleId.eq(roleId))
                .fetchCount();
    }

    /**
     * 通过用户ID删除
     */
    @Override
    public Mono<Long> deleteByUserIds(List<Long> userIds) {
        return queryFactory.delete(tb_1_)
                .where(tb_1_.userId.in(userIds))
                .execute();
    }

    /**
     * 通过用户ID删除
     */
    @Override
    public Mono<Long> deleteByUserId(Long userId) {
        return queryFactory.delete(tb_1_)
                .where(tb_1_.userId.eq(userId))
                .execute();
    }

    @Override
    public int batchUserRole(List<SysUserRole> userRoleList) {
        return 0;
    }

    @Override
    public int deleteUserRoleInfo(SysUserRole userRole) {
        return 0;
    }

    @Override
    public int deleteUserRoleInfos(Long roleId, Long[] userIds) {
        return 0;
    }

    @Override
    public Mono<Long> insert(SysUserRole userRole) {
        return queryFactory.insert(tb_1_)
                .populate(userRole)
                .execute();
    }
}
