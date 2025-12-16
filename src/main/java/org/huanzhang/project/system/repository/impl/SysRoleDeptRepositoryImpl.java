package org.huanzhang.project.system.repository.impl;

import com.querydsl.r2dbc.R2DBCQueryFactory;
import lombok.RequiredArgsConstructor;
import org.huanzhang.project.system.entity.SysRoleDept;
import org.huanzhang.project.system.entity.impl.QSysRoleDept;
import org.huanzhang.project.system.repository.SysRoleDeptRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 角色与菜单关联表 数据处理
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Repository
@RequiredArgsConstructor
public class SysRoleDeptRepositoryImpl implements SysRoleDeptRepository {

    private static final QSysRoleDept tb_1_ = new QSysRoleDept("tb_1_");

    private final R2DBCQueryFactory queryFactory;

    /**
     * 根据角色ID删除
     */
    @Override
    public Mono<Long> deleteByRoleId(Long roleId) {
        return queryFactory.delete(tb_1_)
                .where(tb_1_.roleId.eq(roleId))
                .execute();
    }

    /**
     * 根据角色ID批量删除
     */
    @Override
    public Mono<Long> deleteByRoleIds(List<Long> roleIds) {
        return queryFactory.delete(tb_1_)
                .where(tb_1_.roleId.in(roleIds))
                .execute();
    }

    /**
     * 新增
     */
    @Override
    public Mono<Long> insert(SysRoleDept roleDept) {
        return queryFactory.insert(tb_1_)
                .populate(roleDept)
                .execute();
    }
}
