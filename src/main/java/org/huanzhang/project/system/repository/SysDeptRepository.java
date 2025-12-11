package org.huanzhang.project.system.repository;

import org.huanzhang.project.system.entity.SysDept;
import org.huanzhang.project.system.query.SysDeptQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 部门表 数据层
 *
 * @author haihuoshu
 * @version 2025-12-10
 */
public interface SysDeptRepository {

    /**
     * 根据条件查询部门列表
     */
    Flux<SysDept> selectListByQuery(SysDeptQuery query);

    /**
     * 查询所有下级部门
     */
    Flux<SysDept> selectChildrenByDeptId(Long deptId);

    /**
     * 根据部门ID查询一条数据
     */
    Mono<SysDept> selectOneByDeptId(Long deptId);

    /**
     * 根据上级部门ID和部门名称查询一条数据
     */
    Mono<SysDept> selectOneByParentIdAndDeptName(Long parentId, String deptName);

    /**
     * 根据ID查询所有子部门（正常状态）
     */
    Mono<Long> selectNormalChildrenDeptById(Long deptId);

    /**
     * 是否存在下级
     */
    Mono<Boolean> existsChildByDeptId(Long deptId);

    /**
     * 是否存在用户
     */
    Mono<Boolean> existsUserByDeptId(Long deptId);

    /**
     * 新增部门
     */
    Mono<Long> insertDept(SysDept entity);

    /**
     * 修改部门
     */
    Mono<Long> updateDept(SysDept entity);

    /**
     * 更新部门状态为正常
     */
    Mono<Long> updateDeptStatusNormal(Long[] deptIds);

    /**
     * 修改下级部门
     */
    Mono<Long> updateDeptChildren(List<SysDept> children);

    /**
     * 根据部门ID删除
     */
    Mono<Long> deleteByDeptId(Long deptId);

}
