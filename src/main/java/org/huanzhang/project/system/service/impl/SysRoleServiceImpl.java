package org.huanzhang.project.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.huanzhang.common.exception.ServiceException;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.framework.security.ReactiveSecurityUtils;
import org.huanzhang.project.system.converter.SysRoleMapper;
import org.huanzhang.project.system.dto.SysRoleInsertDTO;
import org.huanzhang.project.system.dto.SysRoleUpdateDTO;
import org.huanzhang.project.system.entity.SysRole;
import org.huanzhang.project.system.entity.SysRoleDept;
import org.huanzhang.project.system.entity.SysRoleMenu;
import org.huanzhang.project.system.entity.SysUserRole;
import org.huanzhang.project.system.query.SysRoleQuery;
import org.huanzhang.project.system.repository.SysRoleDeptRepository;
import org.huanzhang.project.system.repository.SysRoleMenuRepository;
import org.huanzhang.project.system.repository.SysRoleRepository;
import org.huanzhang.project.system.repository.SysUserRoleRepository;
import org.huanzhang.project.system.service.SysRoleService;
import org.huanzhang.project.system.vo.SysRoleVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 角色表 业务处理
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleRepository sysRoleRepository;
    private final SysRoleMapper sysRoleMapper;

    private final SysRoleMenuRepository sysRoleMenuRepository;
    private final SysRoleDeptRepository sysRoleDeptRepository;

    private final SysUserRoleRepository sysUserRoleRepository;

    /**
     * 根据条件查询角色总数
     */
    @Override
    public Mono<Long> selectRoleCountByQuery(SysRoleQuery query) {
        return sysRoleRepository.selectCountByQuery(query);
    }

    /**
     * 根据条件查询角色列表
     */
    @Override
    public Flux<SysRoleVO> selectRoleListByQuery(SysRoleQuery query) {
        return sysRoleRepository.selectListByQuery(query)
                .map(sysRoleMapper::toVo);
    }

    /**
     * 根据角色ID查询详细信息
     */
    @Override
    public Mono<SysRoleVO> selectRoleById(Long roleId) {
        this.checkRoleDataScope(Collections.singletonList(roleId));

        return sysRoleRepository.selectOneByRoleId(roleId)
                .switchIfEmpty(ServiceException.monoInstance("角色不存在"))
                .map(sysRoleMapper::toVo);
    }

    /**
     * 检查角色是否有数据权限
     */
    @Override
    public void checkRoleDataScope(List<Long> roleIds) {
        ReactiveSecurityUtils.getUserId()
                .flatMap(userId -> {
                    if (ReactiveSecurityUtils.isNotAdmin(userId)) {
                        for (Long roleId : roleIds) {
                            SysRoleQuery query = new SysRoleQuery();
                            query.setRoleId(roleId);
                            return sysRoleRepository.selectListByQuery(query)
                                    .hasElements()
                                    .flatMap(hasElements -> {
                                        if (BooleanUtils.isFalse(hasElements)) {
                                            return ServiceException.monoInstance("没有权限访问角色数据");
                                        }
                                        return Mono.empty();
                                    });
                        }
                    }
                    return Mono.empty();
                })
                .subscribe();
    }

    /**
     * 新增角色
     */
    @Transactional
    @Override
    public Mono<Void> insertRole(SysRoleInsertDTO dto) {
        SysRole entity = sysRoleMapper.toEntity(dto);

        return checkRoleNameUnique(entity)
                .then(checkRoleKeyUnique(entity))
                .then(sysRoleRepository.insert(entity))
                .flatMap(roleId -> updateRoleMenu(roleId, dto.getMenuIds()))
                .then();
    }

    /**
     * 检查角色名称是否唯一
     */
    public Mono<Void> checkRoleNameUnique(SysRole role) {
        return sysRoleRepository.selectOneByRoleName(role.getRoleName())
                .flatMap(info -> {
                    if (ObjectUtils.notEqual(info.getRoleId(), role.getRoleId())) {
                        return ServiceException.monoInstance("角色名称已存在");
                    }
                    return Mono.empty();
                })
                .then();
    }

    /**
     * 检查角色权限是否唯一
     */
    public Mono<Void> checkRoleKeyUnique(SysRole role) {
        return sysRoleRepository.selectOneByRoleKey(role.getRoleKey())
                .flatMap(info -> {
                    if (ObjectUtils.notEqual(info.getRoleId(), role.getRoleId())) {
                        return ServiceException.monoInstance("角色权限已存在");
                    }
                    return Mono.empty();
                })
                .then();
    }

    /**
     * 修改角色和菜单关联
     */
    public Mono<Void> updateRoleMenu(Long roleId, List<Long> menuIds) {
        return sysRoleMenuRepository.deleteByRoleId(roleId)
                .thenMany(Flux.fromIterable(menuIds))
                .flatMap(menuId -> {
                    SysRoleMenu rm = new SysRoleMenu();
                    rm.setRoleId(roleId);
                    rm.setMenuId(menuId);
                    return sysRoleMenuRepository.insert(rm);
                })
                .then();
    }

    /**
     * 修改角色
     */
    @Transactional
    @Override
    public Mono<Void> updateRole(SysRoleUpdateDTO dto) {
        this.checkRoleAllowed(new SysRole(dto.getRoleId()));
        this.checkRoleDataScope(Collections.singletonList(dto.getRoleId()));

        SysRole entity = sysRoleMapper.toEntity(dto);

        return checkRoleNameUnique(entity)
                .then(checkRoleKeyUnique(entity))
                .then(sysRoleRepository.selectOneByRoleId(entity.getRoleId()))
                .switchIfEmpty(ServiceException.monoInstance("角色不存在"))
                .then(sysRoleRepository.updateByRoleId(entity))
                .then(updateRoleMenu(entity.getRoleId(), dto.getMenuIds()))
                .then();
    }

    /**
     * 检查角色是否允许操作
     */
    @Override
    public void checkRoleAllowed(SysRole role) {
        if (StringUtils.isNotNull(role.getRoleId()) && role.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    /**
     * 修改角色状态
     */
    @Override
    public Mono<Void> updateRoleStatus(SysRoleUpdateDTO dto) {
        this.checkRoleAllowed(new SysRole(dto.getRoleId()));
        this.checkRoleDataScope(Collections.singletonList(dto.getRoleId()));

        SysRole entity = sysRoleMapper.toEntity(dto);

        return sysRoleRepository.selectOneByRoleId(entity.getRoleId())
                .switchIfEmpty(ServiceException.monoInstance("角色不存在"))
                .then(sysRoleRepository.updateByRoleId(entity))
                .then();
    }

    /**
     * 修改数据权限信息
     *
     * @param dto 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public Mono<Void> updateDataScope(SysRoleUpdateDTO dto) {
        this.checkRoleAllowed(new SysRole(dto.getRoleId()));
        this.checkRoleDataScope(Collections.singletonList(dto.getRoleId()));

        SysRole entity = sysRoleMapper.toEntity(dto);

        return sysRoleRepository.selectOneByRoleId(entity.getRoleId())
                .switchIfEmpty(ServiceException.monoInstance("角色不存在"))
                .then(sysRoleRepository.updateByRoleId(entity))
                .then(updateRoleDept(entity.getRoleId(), dto.getDeptIds()))
                .then();
    }

    /**
     * 修改角色与部门关联
     */
    public Mono<Void> updateRoleDept(Long roleId, List<Long> deptIds) {
        return sysRoleDeptRepository.deleteByRoleId(roleId)
                .thenMany(Flux.fromIterable(deptIds))
                .flatMap(deptId -> {
                    SysRoleDept rd = new SysRoleDept();
                    rd.setRoleId(roleId);
                    rd.setDeptId(deptId);
                    return sysRoleDeptRepository.insert(rd);
                })
                .then();
    }

    /**
     * 批量删除角色
     */
    @Override
    @Transactional
    public Mono<Void> deleteRoleByIds(List<Long> roleIds) {
        return sysRoleRepository.selectListByRoleIds(roleIds)
                .flatMap(role -> {
                    checkRoleAllowed(new SysRole(role.getRoleId()));
                    checkRoleDataScope(Collections.singletonList(role.getRoleId()));

                    return sysUserRoleRepository.selectCountByRoleId(role.getRoleId())
                            .flatMap(count -> {
                                if (count > 0) {
                                    return ServiceException.monoInstance(String.format("%1$s已分配，不能删除", role.getRoleName()));
                                }
                                return Mono.empty();
                            });
                })
                .then(sysRoleRepository.deleteByRoleIds(roleIds))
                .then(sysRoleMenuRepository.deleteByRoleIds(roleIds))
                .then(sysRoleDeptRepository.deleteByRoleIds(roleIds))
                .then();
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Flux<String> selectRolePermissionByUserId(Long userId) {
        return sysRoleRepository.selectListByUserId(userId)
                .map(SysRole::getRoleKey);
    }

    /**
     * 取消授权用户角色
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    @Override
    public int deleteAuthUser(SysUserRole userRole) {
        return sysUserRoleRepository.deleteUserRoleInfo(userRole);
    }

    /**
     * 批量取消授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    @Override
    public int deleteAuthUsers(Long roleId, Long[] userIds) {
        return sysUserRoleRepository.deleteUserRoleInfos(roleId, userIds);
    }

    /**
     * 批量选择授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要授权的用户数据ID
     * @return 结果
     */
    @Override
    public int insertAuthUsers(Long roleId, Long[] userIds) {
        // 新增用户与角色管理
        List<SysUserRole> list = new ArrayList<>();
        for (Long userId : userIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        return sysUserRoleRepository.batchUserRole(list);
    }
}
