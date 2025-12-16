package org.huanzhang.project.system.repository;

import org.apache.ibatis.annotations.Param;
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
     * 通过用户ID删除用户和角色关联
     *
     * @param userId 用户ID
     */
    void deleteUserRoleByUserId(Long userId);

    /**
     * 批量删除用户和角色关联
     *
     * @param ids 需要删除的数据ID
     */
    void deleteUserRole(Long[] ids);

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
    int deleteUserRoleInfos(@Param("roleId") Long roleId, @Param("userIds") Long[] userIds);

}
