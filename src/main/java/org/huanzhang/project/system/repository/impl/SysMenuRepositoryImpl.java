package org.huanzhang.project.system.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.r2dbc.R2DBCQuery;
import com.querydsl.r2dbc.R2DBCQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.huanzhang.common.constant.UserConstants;
import org.huanzhang.framework.security.ReactiveSecurityUtils;
import org.huanzhang.project.system.entity.SysMenu;
import org.huanzhang.project.system.entity.impl.QSysMenu;
import org.huanzhang.project.system.entity.impl.QSysRole;
import org.huanzhang.project.system.entity.impl.QSysRoleMenu;
import org.huanzhang.project.system.entity.impl.QSysUserRole;
import org.huanzhang.project.system.query.SysMenuQuery;
import org.huanzhang.project.system.repository.SysMenuRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 菜单表 数据处理
 *
 * @author haihuoshu
 * @version 2025-12-12
 */
@Repository
@RequiredArgsConstructor
public class SysMenuRepositoryImpl implements SysMenuRepository {

    private static final QSysMenu tb_1_ = new QSysMenu("tb_1_");
    private static final QSysRoleMenu tb_2_ = new QSysRoleMenu("tb_2_");
    private static final QSysRole tb_3_ = new QSysRole("tb_3_");
    private static final QSysUserRole tb_4_ = new QSysUserRole("tb_4_");

    private final R2DBCQueryFactory queryFactory;

    /**
     * 根据条件查询菜单列表
     */
    @Override
    public Flux<SysMenu> selectListByQuery(SysMenuQuery query) {
        BooleanBuilder predicate = getPredicate(query);

        return queryFactory.selectFrom(tb_1_)
                .where(predicate)
                .orderBy(tb_1_.parentId.asc(), tb_1_.orderNum.asc())
                .fetch();
    }

    private static BooleanBuilder getPredicate(SysMenuQuery query) {
        BooleanBuilder predicate = new BooleanBuilder();
        // 菜单名称
        if (StringUtils.isNotBlank(query.getMenuName())) {
            predicate.and(tb_1_.menuName.contains(query.getMenuName()));
        }
        // 显示状态（0显示 1隐藏）
        if (StringUtils.isNotBlank(query.getVisible())) {
            predicate.and(tb_1_.visible.eq(query.getVisible()));
        }
        // 菜单状态（0正常 1停用）
        if (StringUtils.isNotBlank(query.getStatus())) {
            predicate.and(tb_1_.status.eq(query.getStatus()));
        }
        return predicate;
    }

    /**
     * 根据用户查询菜单列表
     */
    @Override
    public Flux<SysMenu> selectListByUserId(Long userId, SysMenuQuery query) {
        BooleanBuilder predicate = getPredicate(query);
        predicate.and(tb_3_.status.eq(UserConstants.NORMAL));
        predicate.and(tb_3_.delFlag.eq(UserConstants.NORMAL));
        predicate.and(tb_4_.userId.eq(userId));

        return queryFactory.selectDistinct(tb_1_)
                .from(tb_1_)
                .leftJoin(tb_2_).on(tb_2_.menuId.eq(tb_1_.menuId))
                .leftJoin(tb_3_).on(tb_3_.roleId.eq(tb_2_.roleId))
                .leftJoin(tb_4_).on(tb_4_.roleId.eq(tb_3_.roleId))
                .where(predicate)
                .orderBy(tb_1_.parentId.asc(), tb_1_.orderNum.asc())
                .fetch();
    }

    /**
     * 根据菜单ID查询
     */
    @Override
    public Mono<SysMenu> selectOneById(Long menuId) {
        return queryFactory.selectDistinct(tb_1_)
                .from(tb_1_)
                .where(tb_1_.menuId.eq(menuId))
                .fetchOne();
    }

    /**
     * 根据上级菜单ID和菜单名称查询
     */
    @Override
    public Mono<SysMenu> selectOneByParentIdAndMenuName(Long parentId, String menuName) {
        return queryFactory.selectDistinct(tb_1_)
                .from(tb_1_)
                .where(tb_1_.parentId.eq(parentId)
                        .and(tb_1_.menuName.eq(menuName))
                )
                .fetchOne();
    }

    /**
     * 新增菜单
     */
    @Override
    public Mono<Long> insertMenu(SysMenu menu) {
        return beforeInsert(menu)
                .flatMap(entity -> {
                    // 执行新增
                    return queryFactory.insert(tb_1_)
                            .populate(entity)
                            .execute();
                });
    }

    /**
     * 修改菜单
     */
    @Override
    public Mono<Long> updateMenu(SysMenu menu) {
        return beforeUpdate(menu)
                .flatMap(entity -> {
                    // 执行修改
                    return queryFactory.update(tb_1_)
                            .populate(entity)
                            .where(tb_1_.menuId.eq(menu.getMenuId()))
                            .execute();
                });
    }

    /**
     * 是否存在菜单子节点
     */
    @Override
    public Mono<Long> selectCountByParentId(Long parentId) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.parentId.eq(parentId))
                .fetchCount();
    }

    /**
     * 根据菜单ID删除
     */
    @Override
    public Mono<Long> deleteByMenuId(Long menuId) {
        return queryFactory.delete(tb_1_)
                .where(tb_1_.menuId.eq(menuId))
                .execute();
    }

    /**
     * 根据用户ID查询菜单
     */
    @Override
    public Flux<SysMenu> selectMenuTreeByUserId(Long userId) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(tb_1_.menuType.in(UserConstants.TYPE_DIR, UserConstants.TYPE_MENU));
        predicate.and(tb_1_.status.eq(UserConstants.NORMAL));

        R2DBCQuery<SysMenu> r2dbcQuery = queryFactory.selectDistinct(tb_1_)
                .from(tb_1_);

        if (ReactiveSecurityUtils.isNotAdmin(userId)) {
            predicate.and(tb_3_.status.eq(UserConstants.NORMAL));

            r2dbcQuery.leftJoin(tb_2_).on(tb_2_.menuId.eq(tb_1_.menuId))
                    .leftJoin(tb_3_).on(tb_3_.roleId.eq(tb_2_.roleId))
                    .leftJoin(tb_4_).on(tb_4_.roleId.eq(tb_3_.roleId));
        }
        return r2dbcQuery.where(predicate)
                .orderBy(tb_1_.parentId.asc(), tb_1_.orderNum.asc())
                .fetch();
    }

}
