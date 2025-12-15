package org.huanzhang.project.system.repository.impl;

import com.querydsl.r2dbc.R2DBCQueryFactory;
import lombok.RequiredArgsConstructor;
import org.huanzhang.project.system.entity.impl.QSysRoleMenu;
import org.huanzhang.project.system.repository.SysRoleMenuRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * 角色与菜单关联表 数据处理
 *
 * @author haihuoshu
 * @version 2025-12-12
 */
@Repository
@RequiredArgsConstructor
public class SysRoleMenuRepositoryImpl implements SysRoleMenuRepository {

    private static final QSysRoleMenu tb_1_ = new QSysRoleMenu("tb_1_");

    private final R2DBCQueryFactory queryFactory;

    @Override
    public Mono<Long> selectCountByMenuId(Long menuId) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.menuId.eq(menuId))
                .fetchCount();
    }

}
