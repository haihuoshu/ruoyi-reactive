package org.huanzhang.project.system.repository;

import org.huanzhang.framework.r2dbc.repository.AuditableRepository;
import org.huanzhang.project.system.entity.SysRole;
import org.huanzhang.project.system.query.SysRoleQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 角色表 数据层
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
public interface SysRoleRepository extends AuditableRepository<SysRole> {

    /**
     * 根据条件查询总数
     */
    Mono<Long> selectCountByQuery(SysRoleQuery query);

    /**
     * 根据条件查询列表
     */
    Flux<SysRole> selectListByQuery(SysRoleQuery query);

    /**
     * 根据角色ID查询
     */
    Mono<SysRole> selectOneByRoleId(Long roleId);

    /**
     * 根据角色名称查询
     */
    Mono<SysRole> selectOneByRoleName(String roleName);

    /**
     * 根据角色权限查询
     */
    Mono<SysRole> selectOneByRoleKey(String roleKey);

    /**
     * 新增
     */
    Mono<Long> insert(SysRole role);

    /**
     * 根据角色ID修改
     */
    Mono<Long> updateByRoleId(SysRole role);

    /**
     * 根据角色ID查询列表
     */
    Flux<SysRole> selectListByRoleIds(List<Long> roleIds);

    /**
     * 根据角色ID批量删除
     */
    Mono<Long> deleteByRoleIds(List<Long> roleIds);

    /**
     * 根据用户ID查询列表
     */
    Flux<SysRole> selectListByUserId(Long userId);

    /**
     * 根据用户ID查询列表
     */
    List<SysRole> selectRolesByUserName(String userName);

}
