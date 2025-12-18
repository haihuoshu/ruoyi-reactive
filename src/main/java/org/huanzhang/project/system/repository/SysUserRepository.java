package org.huanzhang.project.system.repository;

import org.apache.ibatis.annotations.Param;
import org.huanzhang.framework.r2dbc.repository.AuditableRepository;
import org.huanzhang.project.system.dto.SysUserInsertDTO;
import org.huanzhang.project.system.entity.SysUser;
import org.huanzhang.project.system.query.SysUserQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 用户表 数据层
 *
 * @author haihuoshu
 * @version 2025-12-17
 */
public interface SysUserRepository extends AuditableRepository<SysUser> {

    /**
     * 根据条件查询总数
     */
    Mono<Long> selectCountByQuery(SysUserQuery query);

    /**
     * 根据条件查询列表
     */
    Flux<SysUser> selectListByQuery(SysUserQuery query);

    /**
     * 根据用户ID查询
     */
    Mono<SysUser> selectOneByUserId(Long userId);

    /**
     * 根据用户账号查询
     */
    Mono<SysUser> selectOneByUserName(String userName);

    /**
     * 根据手机号码查询
     */
    Mono<SysUser> selectOneByPhonenumber(String phonenumber);

    /**
     * 根据用户邮箱查询
     */
    Mono<SysUser> selectOneByEmail(String email);

    /**
     * 新增用户
     */
    Mono<Long> insertUser(SysUser user);

    /**
     * 修改用户
     */
    Mono<Long> updateUser(SysUser user);

    /**
     * 根据用户ID批量删除
     */
    Mono<Long> deleteByUserIds(List<Long> userIds);

    /**
     * 根据条件分页查询已配用户角色列表
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
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar   头像地址
     * @return 结果
     */
    int updateUserAvatar(@Param("userName") String userName, @Param("avatar") String avatar);

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    int resetUserPwd(@Param("userName") String userName, @Param("password") String password);
}
