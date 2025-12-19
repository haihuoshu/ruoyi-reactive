package org.huanzhang.project.system.repository;

import org.huanzhang.project.system.entity.SysUserRole;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 用户与角色关联表 数据层
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
public interface SysUserRoleRepository {

    /**
     * 通过角色ID查询总数
     */
    Mono<Long> selectCountByRoleId(Long roleId);

    /**
     * 通过用户ID删除
     */
    Mono<Long> deleteByUserIds(List<Long> userIds);

    /**
     * 通过用户ID删除
     */
    Mono<Long> deleteByUserId(Long userId);

    /**
     * 批量新增用户角色信息
     *
     * @param userRoleList 用户角色列表
     * @return 结果
     */
    int batchUserRole(List<SysUserRole> userRoleList);

    /**
     * 删除用户和角色关联信息
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    int deleteUserRoleInfo(SysUserRole userRole);

    /**
     * 批量取消授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    int deleteUserRoleInfos(Long roleId, Long[] userIds);

    Mono<Long> insert(SysUserRole userRole);

}
