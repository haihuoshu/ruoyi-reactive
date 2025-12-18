package org.huanzhang.project.system.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFlag;
import com.querydsl.r2dbc.R2DBCQuery;
import com.querydsl.r2dbc.R2DBCQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.huanzhang.common.constant.UserConstants;
import org.huanzhang.framework.aspectj.lang.annotation.DataScope;
import org.huanzhang.project.system.entity.SysRole;
import org.huanzhang.project.system.entity.impl.QSysDept;
import org.huanzhang.project.system.entity.impl.QSysRole;
import org.huanzhang.project.system.entity.impl.QSysUser;
import org.huanzhang.project.system.entity.impl.QSysUserRole;
import org.huanzhang.project.system.query.SysRoleQuery;
import org.huanzhang.project.system.repository.SysRoleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * 角色表 数据处理
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Repository
@RequiredArgsConstructor
public class SysRoleRepositoryImpl implements SysRoleRepository {

    private static final QSysRole tb_1_ = new QSysRole("tb_1_");
    private static final QSysUserRole tb_2_ = new QSysUserRole("tb_2_");
    private static final QSysUser tb_3_ = new QSysUser("tb_3_");
    private static final QSysDept tb_4_ = new QSysDept("tb_4_");

    private final R2DBCQueryFactory queryFactory;

    /**
     * 根据条件查询总数
     */
    @DataScope(deptAlias = "tb_4_")
    @Override
    public Mono<Long> selectCountByQuery(SysRoleQuery query) {
        return getR2dbcQuery(query)
                .fetchCount();
    }

    private R2DBCQuery<SysRole> getR2dbcQuery(SysRoleQuery query) {
        BooleanBuilder predicate = new BooleanBuilder(tb_1_.delFlag.eq(UserConstants.NORMAL));
        // 角色ID
        if (Objects.nonNull(query.getRoleId())) {
            predicate.and(tb_1_.roleId.eq(query.getRoleId()));
        }
        // 角色名称
        if (StringUtils.isNotBlank(query.getRoleName())) {
            predicate.and(tb_1_.roleName.contains(query.getRoleName()));
        }
        // 角色权限
        if (StringUtils.isNotBlank(query.getRoleKey())) {
            predicate.and(tb_1_.roleName.contains(query.getRoleKey()));
        }
        // 状态（0正常 1停用）
        if (StringUtils.isNotBlank(query.getStatus())) {
            predicate.and(tb_1_.status.eq(query.getStatus()));
        }
        return queryFactory.selectDistinct(tb_1_)
                .from(tb_1_)
                .leftJoin(tb_2_).on(tb_2_.roleId.eq(tb_1_.roleId))
                .leftJoin(tb_3_).on(tb_3_.userId.eq(tb_2_.userId).and(tb_4_.delFlag.eq(UserConstants.NORMAL)))
                .leftJoin(tb_4_).on(tb_4_.deptId.eq(tb_3_.deptId).and(tb_4_.delFlag.eq(UserConstants.NORMAL)))
                .where(predicate)
                .addFlag(QueryFlag.Position.AFTER_FILTERS, query.getDataScope());
    }

    /**
     * 根据条件查询列表
     */
    @DataScope(deptAlias = "tb_4_")
    @Override
    public Flux<SysRole> selectListByQuery(SysRoleQuery query) {
        R2DBCQuery<SysRole> r2dbcQuery = getR2dbcQuery(query);

        if (ObjectUtils.allNotNull(query.getPageNum(), query.getPageSize())) {
            PageRequest pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize());
            r2dbcQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        return r2dbcQuery.orderBy(tb_1_.roleSort.asc())
                .fetch();
    }

    /**
     * 根据主键查询
     */
    @Override
    public Mono<SysRole> selectOneByRoleId(Long roleId) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.roleId.eq(roleId))
                .fetchOne();
    }

    /**
     * 根据角色名称查询
     */
    @Override
    public Mono<SysRole> selectOneByRoleName(String roleName) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.roleName.eq(roleName)
                        .and(tb_1_.delFlag.eq(UserConstants.NORMAL))
                )
                .fetchOne();
    }

    /**
     * 根据角色权限查询
     */
    @Override
    public Mono<SysRole> selectOneByRoleKey(String roleKey) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.roleKey.eq(roleKey)
                        .and(tb_1_.delFlag.eq(UserConstants.NORMAL))
                )
                .fetchOne();
    }

    /**
     * 新增角色
     */
    @Override
    public Mono<Long> insert(SysRole role) {
        return beforeInsert(role)
                .flatMap(entity -> {
                    entity.setDelFlag(UserConstants.NORMAL);
                    // 执行新增
                    return queryFactory.insert(tb_1_)
                            .populate(entity)
                            .executeWithKey(tb_1_.roleId);
                });
    }

    /**
     * 修改角色
     */
    @Override
    public Mono<Long> updateByRoleId(SysRole role) {
        return beforeUpdate(role)
                .flatMap(entity -> {
                    // 执行修改
                    return queryFactory.update(tb_1_)
                            .populate(entity)
                            .where(tb_1_.roleId.eq(entity.getRoleId()))
                            .execute();
                });
    }

    /**
     * 根据主键查询列表
     */
    @Override
    public Flux<SysRole> selectListByRoleIds(List<Long> roleIds) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.roleId.in(roleIds))
                .fetch();
    }

    /**
     * 根据角色ID批量删除
     */
    @Override
    public Mono<Long> deleteByRoleIds(List<Long> roleIds) {
        return queryFactory.update(tb_1_)
                .set(tb_1_.delFlag, UserConstants.DEPT_DISABLE)
                .where(tb_1_.roleId.in(roleIds))
                .execute();
    }

    /**
     * 根据用户ID查询角色
     */
    @Override
    public Flux<SysRole> selectListByUserId(Long userId) {
        return queryFactory.selectDistinct(tb_1_)
                .from(tb_1_)
                .leftJoin(tb_2_).on(tb_2_.roleId.eq(tb_1_.roleId))
                .where(tb_1_.delFlag.eq(UserConstants.NORMAL)
                        .and(tb_2_.userId.eq(userId))
                )
                .orderBy(tb_1_.roleSort.asc())
                .fetch();
    }

    @Override
    public List<SysRole> selectRolesByUserName(String userName) {
        return List.of();
    }
}
