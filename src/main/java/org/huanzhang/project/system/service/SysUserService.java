package org.huanzhang.project.system.service;

import org.huanzhang.project.system.dto.SysUserInsertDTO;
import org.huanzhang.project.system.dto.SysUserUpdateDTO;
import org.huanzhang.project.system.query.SysUserQuery;
import org.huanzhang.project.system.vo.SysUserVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 用户表 业务层
 *
 * @author haihuoshu
 * @version 2025-12-17
 */
public interface SysUserService {

    /**
     * 根据条件查询用户总数
     */
    Mono<Long> selectUserCountByQuery(SysUserQuery query);

    /**
     * 根据条件查询用户列表
     */
    Flux<SysUserVO> selectUserListByQuery(SysUserQuery query);

    /**
     * 根据用户ID查询详细信息
     */
    Mono<SysUserVO> selectUserById(Long userId);

    /**
     * 校验用户是否有数据权限
     */
    void checkUserDataScope(Long userId);

    /**
     * 新增用户
     */
    Mono<Void> insertUser(SysUserInsertDTO dto);

    /**
     * 修改用户
     */
    Mono<Void> updateUser(SysUserUpdateDTO dto);

    /**
     * 修改用户密码
     */
    Mono<Void> updateUserPassword(SysUserUpdateDTO dto);

    /**
     * 修改用户状态
     */
    Mono<Void> updateUserStatus(SysUserUpdateDTO dto);

    /**
     * 用户授权角色
     */
    Mono<Void> updateUserRole(SysUserUpdateDTO dto);

    /**
     * 批量删除用户
     */
    Mono<Void> deleteUserByIds(List<Long> userIds);

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SysUserInsertDTO> selectAllocatedList(SysUserInsertDTO user);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SysUserInsertDTO> selectUnallocatedList(SysUserInsertDTO user);

    /**
     * 根据用户ID查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    String selectUserRoleGroup(String userName);

    /**
     * 根据用户ID查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    String selectUserPostGroup(String userName);

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUserProfile(SysUserInsertDTO user);

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar   头像地址
     * @return 结果
     */
    boolean updateUserAvatar(String userName, String avatar);

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    int resetUserPwd(String userName, String password);

}
