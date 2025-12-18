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
import org.huanzhang.project.system.dto.SysUserInsertDTO;
import org.huanzhang.project.system.entity.SysUser;
import org.huanzhang.project.system.entity.impl.QSysDept;
import org.huanzhang.project.system.entity.impl.QSysUser;
import org.huanzhang.project.system.query.SysUserQuery;
import org.huanzhang.project.system.repository.SysUserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/**
 * 用户表 数据处理
 *
 * @author haihuoshu
 * @version 2025-12-17
 */
@Repository
@RequiredArgsConstructor
public class SysUserRepositoryImpl implements SysUserRepository {

    private final QSysUser tb_1_ = new QSysUser("tb_1_");
    private final QSysDept tb_2_ = new QSysDept("tb_2_");

    private final R2DBCQueryFactory queryFactory;

    /**
     * 根据条件查询总数
     */
    @DataScope(deptAlias = "tb_2_", userAlias = "tb_1_")
    @Override
    public Mono<Long> selectCountByQuery(SysUserQuery query) {
        return getR2dbcQuery(query)
                .fetchCount();
    }

    private R2DBCQuery<SysUser> getR2dbcQuery(SysUserQuery query) {
        BooleanBuilder predicate = new BooleanBuilder(tb_1_.delFlag.eq(UserConstants.NORMAL));
        // 用户ID
        if (Objects.nonNull(query.getUserId())) {
            predicate.and(tb_1_.userId.eq(query.getUserId()));
        }
        // 用户账号
        if (StringUtils.isNotBlank(query.getUserName())) {
            predicate.and(tb_1_.userName.contains(query.getUserName()));
        }
        // 手机号码
        if (StringUtils.isNotBlank(query.getPhonenumber())) {
            predicate.and(tb_1_.phonenumber.contains(query.getPhonenumber()));
        }
        // 状态（0正常 1停用）
        if (StringUtils.isNotBlank(query.getStatus())) {
            predicate.and(tb_1_.status.eq(query.getStatus()));
        }
        // 创建时间
        if (ObjectUtils.allNotNull(query.getStartTime())) {
            predicate.and(tb_1_.createTime.goe(LocalDateTime.of(query.getStartTime(), LocalTime.MIN)));
        }
        if (ObjectUtils.allNotNull(query.getEndTime())) {
            predicate.and(tb_1_.createTime.loe(LocalDateTime.of(query.getEndTime(), LocalTime.MAX)));
        }
        // 部门ID
        if (Objects.nonNull(query.getDeptId())) {
            predicate.and(tb_2_.deptId.eq(query.getDeptId()));
        }

        return queryFactory.selectDistinct(tb_1_)
                .from(tb_1_)
                .leftJoin(tb_2_).on(tb_2_.deptId.eq(tb_1_.deptId).and(tb_2_.delFlag.eq(UserConstants.NORMAL)))
                .where(predicate)
                .addFlag(QueryFlag.Position.AFTER_FILTERS, query.getDataScope());
    }

    /**
     * 根据条件查询列表
     */
    @DataScope(deptAlias = "tb_2_", userAlias = "tb_1_")
    @Override
    public Flux<SysUser> selectListByQuery(SysUserQuery query) {
        R2DBCQuery<SysUser> r2dbcQuery = getR2dbcQuery(query);

        if (ObjectUtils.allNotNull(query.getPageNum(), query.getPageSize())) {
            PageRequest pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize());
            r2dbcQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        return r2dbcQuery.orderBy(tb_1_.createTime.desc())
                .fetch();
    }

    /**
     * 根据用户ID查询
     */
    @Override
    public Mono<SysUser> selectOneByUserId(Long userId) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.userId.eq(userId).and(tb_1_.delFlag.eq(UserConstants.NORMAL)))
                .fetchOne();
    }

    /**
     * 根据用户账号查询
     */
    @Override
    public Mono<SysUser> selectOneByUserName(String userName) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.userName.eq(userName).and(tb_1_.delFlag.eq(UserConstants.NORMAL)))
                .fetchOne();
    }

    /**
     * 根据手机号码查询
     */
    @Override
    public Mono<SysUser> selectOneByPhonenumber(String phonenumber) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.phonenumber.eq(phonenumber).and(tb_1_.delFlag.eq(UserConstants.NORMAL)))
                .fetchOne();
    }

    /**
     * 根据用户邮箱查询
     */
    @Override
    public Mono<SysUser> selectOneByEmail(String email) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.email.eq(email).and(tb_1_.delFlag.eq(UserConstants.NORMAL)))
                .fetchOne();
    }

    /**
     * 新增用户
     */
    @Override
    public Mono<Long> insertUser(SysUser user) {
        return beforeInsert(user)
                .flatMap(entity -> {
                    // 执行新增
                    return queryFactory.insert(tb_1_)
                            .populate(entity)
                            .executeWithKey(tb_1_.userId);
                });
    }

    /**
     * 修改用户
     */
    @Override
    public Mono<Long> updateUser(SysUser user) {
        return beforeUpdate(user)
                .flatMap(entity -> {
                    // 执行修改
                    return queryFactory.update(tb_1_)
                            .populate(entity)
                            .where(tb_1_.userId.eq(entity.getUserId()))
                            .execute();
                });
    }

    /**
     * 根据用户ID批量删除
     */
    @Override
    public Mono<Long> deleteByUserIds(List<Long> userIds) {
        return queryFactory.update(tb_1_)
                .set(tb_1_.delFlag, UserConstants.DEPT_DISABLE)
                .where(tb_1_.userId.in(userIds))
                .execute();
    }

    @Override
    public List<SysUserInsertDTO> selectAllocatedList(SysUserInsertDTO user) {
        return List.of();
    }

    @Override
    public List<SysUserInsertDTO> selectUnallocatedList(SysUserInsertDTO user) {
        return List.of();
    }


    @Override
    public int updateUserAvatar(String userName, String avatar) {
        return 0;
    }

    @Override
    public int resetUserPwd(String userName, String password) {
        return 0;
    }

}
