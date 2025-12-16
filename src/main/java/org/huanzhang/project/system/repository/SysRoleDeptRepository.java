package org.huanzhang.project.system.repository;

import org.huanzhang.project.system.entity.SysRoleDept;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 角色与部门关联表 数据层
 *
 * @author haihuoshu
 * @version 2025-12-12
 */
public interface SysRoleDeptRepository {

    /**
     * 根据角色ID删除
     */
    Mono<Long> deleteByRoleId(Long roleId);

    /**
     * 根据角色ID批量删除
     */
    Mono<Long> deleteByRoleIds(List<Long> roleIds);

    /**
     * 新增
     */
    Mono<Long> insert(SysRoleDept roleDept);

}
