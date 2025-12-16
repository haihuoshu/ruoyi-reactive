package org.huanzhang.project.system.service;

import org.huanzhang.project.system.dto.SysRoleInsertDTO;
import org.huanzhang.project.system.dto.SysRoleUpdateDTO;
import org.huanzhang.project.system.entity.SysRole;
import org.huanzhang.project.system.entity.SysUserRole;
import org.huanzhang.project.system.query.SysRoleQuery;
import org.huanzhang.project.system.vo.SysRoleVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 角色表 业务层
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
public interface SysRoleService {

    /**
     * 根据条件查询角色总数
     */
    Mono<Long> selectRoleCountByQuery(SysRoleQuery query);

    /**
     * 根据条件查询角色列表
     */
    Flux<SysRoleVO> selectRoleListByQuery(SysRoleQuery query);

    /**
     * 根据角色ID查询详细信息
     */
    Mono<SysRoleVO> selectRoleById(Long roleId);

    /**
     * 检查角色是否有数据权限
     */
    void checkRoleDataScope(Long... roleIds);

    /**
     * 新增角色
     */
    Mono<Void> insertRole(SysRoleInsertDTO dto);

    /**
     * 修改角色
     */
    Mono<Void> updateRole(SysRoleUpdateDTO dto);

    /**
     * 检查角色是否允许操作
     */
    void checkRoleAllowed(SysRole role);

    /**
     * 修改角色状态
     */
    Mono<Void> updateRoleStatus(SysRoleUpdateDTO dto);

    /**
     * 修改数据权限
     */
    Mono<Void> updateDataScope(SysRoleUpdateDTO dto);

    /**
     * 批量删除角色
     */
    Mono<Void> deleteRoleByIds(List<Long> roleIds);

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Flux<String> selectRolePermissionByUserId(Long userId);

    /**
     * 取消授权用户角色
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    int deleteAuthUser(SysUserRole userRole);

    /**
     * 批量取消授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    int deleteAuthUsers(Long roleId, Long[] userIds);

    /**
     * 批量选择授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    int insertAuthUsers(Long roleId, Long[] userIds);
}
