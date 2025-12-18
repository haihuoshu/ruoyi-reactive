package org.huanzhang.project.system.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.huanzhang.common.exception.ServiceException;
import org.huanzhang.common.utils.SecurityUtils;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.framework.aspectj.lang.annotation.DataScope;
import org.huanzhang.framework.security.ReactiveSecurityUtils;
import org.huanzhang.project.system.converter.SysUserMapper;
import org.huanzhang.project.system.dto.SysUserInsertDTO;
import org.huanzhang.project.system.dto.SysUserUpdateDTO;
import org.huanzhang.project.system.entity.SysRole;
import org.huanzhang.project.system.entity.SysUser;
import org.huanzhang.project.system.entity.SysUserPost;
import org.huanzhang.project.system.entity.SysUserRole;
import org.huanzhang.project.system.query.SysUserQuery;
import org.huanzhang.project.system.repository.SysRoleRepository;
import org.huanzhang.project.system.repository.SysUserPostRepository;
import org.huanzhang.project.system.repository.SysUserRepository;
import org.huanzhang.project.system.repository.SysUserRoleRepository;
import org.huanzhang.project.system.service.SysDeptService;
import org.huanzhang.project.system.service.SysRoleService;
import org.huanzhang.project.system.service.SysUserService;
import org.huanzhang.project.system.vo.SysUserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户表 业务处理
 *
 * @author haihuoshu
 * @version 2025-12-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserRepository sysUserRepository;
    private final SysUserMapper sysUserMapper;

    private final SysUserRoleRepository sysUserRoleRepository;
    private final SysUserPostRepository sysUserPostRepository;

    private final SysRoleRepository sysRoleRepository;

    private final SysDeptService deptService;
    private final SysRoleService roleService;

    /**
     * 根据条件查询用户总数
     */
    @Override
    public Mono<Long> selectUserCountByQuery(SysUserQuery query) {
        return sysUserRepository.selectCountByQuery(query);
    }

    /**
     * 根据条件查询用户列表
     */
    @Override
    public Flux<SysUserVO> selectUserListByQuery(SysUserQuery query) {
        return sysUserRepository.selectListByQuery(query)
                .map(sysUserMapper::toVo);
    }

    /**
     * 根据条件查询用户列表
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUserInsertDTO> selectAllocatedList(SysUserInsertDTO user) {
        return sysUserRepository.selectAllocatedList(user);
    }

    /**
     * 根据用户ID查询详细信息
     */
    @Override
    public Mono<SysUserVO> selectUserById(Long userId) {
        checkUserDataScope(userId);

        return sysUserRepository.selectOneByUserId(userId)
                .switchIfEmpty(ServiceException.monoInstance("用户不存在"))
                .map(sysUserMapper::toVo);
    }

    /**
     * 检查用户是否有数据权限
     *
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId) {
        if (!SysUser.isAdmin(SecurityUtils.getUserId())) {
            SysUserQuery query = new SysUserQuery();
            query.setUserId(userId);
            sysUserRepository.selectListByQuery(query)
                    .hasElements()
                    .subscribe(has -> {
                        if (BooleanUtils.isFalse(has)) {
                            throw new ServiceException("没有权限访问用户数据！");
                        }
                    });
        }
    }

    /**
     * 新增用户
     */
    @Transactional
    @Override
    public Mono<Void> insertUser(SysUserInsertDTO dto) {
        deptService.checkDeptDataScope(dto.getDeptId());
        roleService.checkRoleDataScope(dto.getRoleIds());

        SysUser entity = sysUserMapper.toEntity(dto);
        return checkUserNameUnique(entity)
                .then(checkPhoneUnique(entity))
                .then(checkEmailUnique(entity))
                .then(Mono.defer(() -> {
                    dto.setPassword(SecurityUtils.encryptPassword(dto.getPassword()));
                    // 新增用户信息
                    return sysUserRepository.insertUser(entity)
                            .flatMap(userId -> {
                                // 新增用户与岗位关联
                                return insertUserPost(userId, dto.getPostIds())
                                        // 新增用户与角色关联
                                        .then(insertUserRole(userId, dto.getRoleIds()));
                            });
                }))
                .then();
    }

    /**
     * 检查用户账号是否唯一
     */
    private Mono<Void> checkUserNameUnique(SysUser user) {
        return sysUserRepository.selectOneByUserName(user.getUserName())
                .flatMap(info -> {
                    if (ObjectUtils.notEqual(info.getUserId(), user.getUserId())) {
                        return ServiceException.monoInstance("用户账号已存在");
                    }
                    return Mono.empty();
                })
                .then();
    }

    /**
     * 检查手机号码是否唯一
     */
    private Mono<Void> checkPhoneUnique(SysUser user) {
        if (StringUtils.isBlank(user.getPhonenumber())) {
            return Mono.empty();
        }
        return sysUserRepository.selectOneByPhonenumber(user.getPhonenumber())
                .flatMap(info -> {
                    if (ObjectUtils.notEqual(info.getUserId(), user.getUserId())) {
                        return ServiceException.monoInstance("手机号码已存在");
                    }
                    return Mono.empty();
                })
                .then();
    }

    /**
     * 检查用户邮箱是否唯一
     */
    private Mono<Void> checkEmailUnique(SysUser user) {
        if (StringUtils.isBlank(user.getEmail())) {
            return Mono.empty();
        }
        return sysUserRepository.selectOneByEmail(user.getEmail())
                .flatMap(info -> {
                    if (ObjectUtils.notEqual(info.getUserId(), user.getUserId())) {
                        return ServiceException.monoInstance("用户邮箱已存在");
                    }
                    return Mono.empty();
                })
                .then();
    }

    /**
     * 新增用户岗位
     */
    public Mono<Void> insertUserPost(Long userId, List<Long> postIds) {
        return Flux.fromIterable(postIds)
                .flatMap(postId -> {
                    SysUserPost userPost = new SysUserPost();
                    userPost.setUserId(userId);
                    userPost.setPostId(postId);
                    return sysUserPostRepository.insert(userPost);
                })
                .then();
    }

    /**
     * 新增用户角色
     */
    public Mono<Void> insertUserRole(Long userId, List<Long> roleIds) {
        return Flux.fromIterable(roleIds)
                .flatMap(roleId -> {
                    SysUserRole userPost = new SysUserRole();
                    userPost.setUserId(userId);
                    userPost.setRoleId(roleId);
                    return sysUserRoleRepository.insert(userPost);
                })
                .then();
    }

    /**
     * 修改用户
     */
    @Transactional
    @Override
    public Mono<Void> updateUser(SysUserUpdateDTO dto) {
        checkUserAllowed(dto.getUserId());
        checkUserDataScope(dto.getUserId());
        deptService.checkDeptDataScope(dto.getDeptId());
        roleService.checkRoleDataScope(dto.getRoleIds());

        SysUser entity = sysUserMapper.toEntity(dto);
        return checkUserNameUnique(entity)
                .then(checkPhoneUnique(entity))
                .then(checkEmailUnique(entity))
                .then(sysUserRepository.selectOneByUserId(dto.getUserId()))
                .switchIfEmpty(ServiceException.monoInstance("用户不存在"))
                .flatMap(user -> {
                    // 更新用户信息
                    return sysUserRepository.updateUser(entity)
                            // 更新用户与岗位关联
                            .then(sysUserPostRepository.deleteByUserId(user.getUserId()).then(insertUserPost(user.getUserId(), dto.getPostIds())))
                            // 更新用户与角色关联
                            .then(sysUserRoleRepository.deleteByUserId(user.getUserId()).then(insertUserRole(user.getUserId(), dto.getPostIds())));
                })
                .then();
    }

    /**
     * 检查用户是否允许操作
     */
    public void checkUserAllowed(Long userId) {
        if (SysUser.isAdmin(userId)) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 修改用户密码
     */
    @Override
    public Mono<Void> updateUserPassword(SysUserUpdateDTO dto) {
        checkUserAllowed(dto.getUserId());
        checkUserDataScope(dto.getUserId());

        return sysUserRepository.selectOneByUserId(dto.getUserId())
                .switchIfEmpty(ServiceException.monoInstance("用户不存在"))
                .flatMap(user -> {
                    if (org.apache.commons.lang3.StringUtils.isBlank(dto.getPassword())) {
                        return ServiceException.monoInstance("密码不能为空");
                    }
                    // 更新用户信息
                    user.setPassword(SecurityUtils.encryptPassword(dto.getPassword()));
                    return sysUserRepository.updateUser(user);
                })
                .then();
    }

    /**
     * 修改用户状态
     */
    @Override
    public Mono<Void> updateUserStatus(SysUserUpdateDTO dto) {
        checkUserAllowed(dto.getUserId());
        checkUserDataScope(dto.getUserId());

        return sysUserRepository.selectOneByUserId(dto.getUserId())
                .switchIfEmpty(ServiceException.monoInstance("用户不存在"))
                .flatMap(user -> {
                    if (org.apache.commons.lang3.StringUtils.isBlank(dto.getStatus())) {
                        return ServiceException.monoInstance("状态不能为空");
                    }
                    // 更新用户信息
                    user.setStatus(dto.getStatus());
                    return sysUserRepository.updateUser(user);
                })
                .then();
    }

    /**
     * 用户授权角色
     */
    @Transactional
    @Override
    public Mono<Void> updateUserRole(SysUserUpdateDTO dto) {
        checkUserAllowed(dto.getUserId());
        checkUserDataScope(dto.getUserId());
        roleService.checkRoleDataScope(dto.getRoleIds());

        return sysUserRepository.selectOneByUserId(dto.getUserId())
                .switchIfEmpty(ServiceException.monoInstance("用户不存在"))
                .flatMap(user -> {
                    // 更新用户与角色关联
                    return sysUserRoleRepository.deleteByUserId(user.getUserId())
                            .then(insertUserRole(user.getUserId(), dto.getPostIds()));
                })
                .then();
    }

    /**
     * 批量删除用户
     */
    @Override
    @Transactional
    public Mono<Void> deleteUserByIds(List<Long> userIds) {
        return ReactiveSecurityUtils.getUserId()
                .flatMap(userId -> {
                    if (org.apache.commons.collections4.CollectionUtils.containsAny(userIds, userId)) {
                        return ServiceException.monoInstance("当前用户不能删除");
                    }
                    return Mono.empty();
                })
                .thenMany(Flux.fromIterable(userIds))
                .flatMap(userId -> {
                    checkUserAllowed(userId);
                    checkUserDataScope(userId);
                    return Mono.empty();
                })
                // 删除用户
                .then(sysUserRepository.deleteByUserIds(userIds))
                // 删除用户与岗位关联
                .then(sysUserPostRepository.deleteByUserIds(userIds))
                // 删除用户与角色关联
                .then(sysUserRoleRepository.deleteByUserIds(userIds))
                .then();
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUserInsertDTO> selectUnallocatedList(SysUserInsertDTO user) {
        return sysUserRepository.selectUnallocatedList(user);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName) {
        List<SysRole> list = sysRoleRepository.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName) {
//        List<SysPost> list = postMapper.selectPostsByUserName(userName);
//        if (CollectionUtils.isEmpty(list)) {
        return StringUtils.EMPTY;
//        }
//        return list.stream().map(SysPost::getPostName).collect(Collectors.joining(","));
    }


    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUserInsertDTO user) {
        sysUserRepository.updateUser(sysUserMapper.toEntity(user)).subscribe();
        return 1;
    }

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar   头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String userName, String avatar) {
        return sysUserRepository.updateUserAvatar(userName, avatar) > 0;
    }

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(String userName, String password) {
        return sysUserRepository.resetUserPwd(userName, password);
    }

}
