package org.huanzhang.project.system.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.r2dbc.R2DBCQuery;
import com.querydsl.r2dbc.R2DBCQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.huanzhang.common.constant.UserConstants;
import org.huanzhang.framework.aspectj.lang.annotation.DataScope;
import org.huanzhang.framework.security.ReactiveSecurityUtils;
import org.huanzhang.project.system.entity.QSysDept;
import org.huanzhang.project.system.entity.QSysUser;
import org.huanzhang.project.system.entity.SysDept;
import org.huanzhang.project.system.query.SysDeptQuery;
import org.huanzhang.project.system.repository.SysDeptRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 部门表 数据处理
 *
 * @author haihuoshu
 * @version 2025-12-10
 */
@Repository
@RequiredArgsConstructor
public class SysDeptRepositoryImpl implements SysDeptRepository {

    private static final QSysDept tb_1_ = new QSysDept("tb_1_");
    private static final QSysUser tb_2_ = new QSysUser("tb_2_");

    private final R2DBCQueryFactory queryFactory;

    /**
     * 根据条件查询部门列表
     */
    @DataScope(deptAlias = "tb_1_")
    @Override
    public Flux<SysDept> selectListByQuery(SysDeptQuery query) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(tb_1_.delFlag.eq(UserConstants.NORMAL));

        if (ObjectUtils.allNotNull(query.getDeptId())) {
            predicate.and(tb_1_.deptId.eq(query.getDeptId()));
        }
        if (ObjectUtils.allNotNull(query.getParentId())) {
            predicate.and(tb_1_.parentId.eq(query.getParentId()));
        }
        if (StringUtils.isNotBlank(query.getDeptName())) {
            predicate.and(tb_1_.deptName.contains(query.getDeptName()));
        }
        if (StringUtils.isNotBlank(query.getStatus())) {
            predicate.and(tb_1_.status.eq(query.getStatus()));
        }

        R2DBCQuery<SysDept> r2dbcQuery = queryFactory.selectFrom(tb_1_)
                .where(predicate)
                .orderBy(tb_1_.parentId.asc(), tb_1_.orderNum.asc());

        r2dbcQuery.addFlag(QueryFlag.Position.AFTER_FILTERS, query.getDataScope());

        return r2dbcQuery.fetch();
    }

    /**
     * 查询所有下级部门
     */
    @Override
    public Flux<SysDept> selectChildrenByDeptId(Long deptId) {
        return queryFactory.selectFrom(tb_1_)
                .where(Expressions.booleanTemplate("find_in_set({0}, {1})", deptId, tb_1_.ancestors))
                .fetch();
    }

    /**
     * 根据部门ID查询一条数据
     */
    @Override
    public Mono<SysDept> selectOneByDeptId(Long deptId) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.deptId.eq(deptId))
                .fetchOne();
    }

    /**
     * 根据上级部门ID和部门名称查询一条数据
     */
    @Override
    public Mono<SysDept> selectOneByParentIdAndDeptName(Long parentId, String deptName) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.parentId.eq(parentId)
                        .and(tb_1_.deptName.eq(deptName))
                        .and(tb_1_.delFlag.eq(UserConstants.NORMAL))
                )
                .fetchOne();
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     */
    @Override
    public Mono<Long> selectNormalChildrenDeptById(Long deptId) {
        return queryFactory.selectFrom(tb_1_)
                .where(Expressions.booleanTemplate("find_in_set({0}, {1})", deptId, tb_1_.ancestors)
                        .and(tb_1_.status.eq(UserConstants.NORMAL))
                        .and(tb_1_.delFlag.eq(UserConstants.NORMAL))
                )
                .fetchCount();
    }

    /**
     * 是否存在下级
     */
    @Override
    public Mono<Boolean> existsChildByDeptId(Long deptId) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.parentId.eq(deptId)
                        .and(tb_1_.delFlag.eq(UserConstants.NORMAL))
                )
                .limit(1)
                .fetchCount()
                .map(count -> count > 0);
    }

    /**
     * 是否存在用户
     */
    @Override
    public Mono<Boolean> existsUserByDeptId(Long deptId) {
        return queryFactory.selectFrom(tb_2_)
                .where(tb_2_.deptId.eq(deptId)
                        .and(tb_2_.delFlag.eq(UserConstants.NORMAL))
                )
                .limit(1)
                .fetchCount()
                .map(count -> count > 0);
    }

    /**
     * 新增部门
     */
    @Override
    public Mono<Long> insertDept(SysDept entity) {
        return ReactiveSecurityUtils.getUsername()
                .flatMap(username -> {
                    entity.setCreateBy(username);
                    entity.setCreateTime(LocalDateTime.now());
                    entity.setUpdateTime(entity.getCreateTime());
                    entity.setUpdateBy(entity.getCreateBy());
                    return queryFactory.insert(tb_1_)
                            .populate(entity)
                            .execute();
                });
    }

    /**
     * 修改部门
     */
    @Override
    public Mono<Long> updateDept(SysDept entity) {
        return ReactiveSecurityUtils.getUsername()
                .flatMap(username -> {
                    entity.setUpdateBy(username);
                    entity.setUpdateTime(LocalDateTime.now());
                    return queryFactory.update(tb_1_)
                            .populate(entity)
                            .where(tb_1_.deptId.eq(entity.getDeptId()))
                            .execute();
                });
    }

    /**
     * 更新部门状态为正常
     */
    @Override
    public Mono<Long> updateDeptStatusNormal(Long[] deptIds) {
        return ReactiveSecurityUtils.getUsername()
                .flatMap(updateBy -> {
                    // 执行更新
                    return queryFactory.update(tb_1_)
                            .set(tb_1_.status, UserConstants.NORMAL)
                            .set(tb_1_.updateBy, updateBy)
                            .set(tb_1_.updateTime, LocalDateTime.now())
                            .where(tb_1_.deptId.in(deptIds))
                            .execute();
                });
    }

    /**
     * 修改下级部门
     */
    @Override
    public Mono<Long> updateDeptChildren(List<SysDept> children) {
        return ReactiveSecurityUtils.getUsername()
                .flatMap(updateBy -> {
                    // 执行更新
                    return queryFactory.update(tb_1_)
                            .set(tb_1_.ancestors, buildDynamicCases(children))
                            .set(tb_1_.updateBy, updateBy)
                            .set(tb_1_.updateTime, LocalDateTime.now())
                            .where(tb_1_.deptId.in(children.stream().map(SysDept::getDeptId).toList()))

                            .execute();
                });
    }

    private Expression<String> buildDynamicCases(List<SysDept> deptList) {
        CaseBuilder.Cases<String, StringExpression> cases = null;
        // 构建 CASE WHEN 表达式
        for (SysDept dept : deptList) {
            if (Objects.isNull(cases)) {
                cases = Expressions.cases().when(tb_1_.deptId.eq(dept.getDeptId())).then(dept.getAncestors());
            } else {
                cases = cases.when(tb_1_.deptId.eq(dept.getDeptId())).then(dept.getAncestors());
            }
        }

        // 默认
        if (Objects.isNull(cases)) {
            return Expressions.stringTemplate("{0}", tb_1_.ancestors);
        } else {
            return cases.otherwise(tb_1_.ancestors);
        }
    }

    /**
     * 根据部门ID删除
     */
    @Override
    public Mono<Long> deleteByDeptId(Long deptId) {
        return queryFactory.update(tb_1_)
                .set(tb_1_.delFlag, UserConstants.DEPT_DISABLE)
                .where(tb_1_.deptId.eq(deptId))
                .execute();
    }

}
