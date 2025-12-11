package org.huanzhang.project.system.service;

import org.huanzhang.project.system.dto.SysDeptInsertDTO;
import org.huanzhang.project.system.dto.SysDeptUpdateDTO;
import org.huanzhang.project.system.query.SysDeptQuery;
import org.huanzhang.project.system.vo.SysDeptVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 部门表 业务层
 *
 * @author haihuoshu
 * @version 2025-12-10
 */
public interface SysDeptService {

    /**
     * 根据条件查询部门列表
     */
    Flux<SysDeptVO> selectDeptList(SysDeptQuery query);

    /**
     * 查询部门列表（排除节点）
     */
    Flux<SysDeptVO> selectDeptListExclude(Long deptId);

    /**
     * 根据部门ID查询详细信息
     */
    Mono<SysDeptVO> selectDeptById(Long deptId);

    /**
     * 校验部门是否有数据权限
     */
    void checkDeptDataScope(Long deptId);

    /**
     * 新增部门
     */
    Mono<Void> insertDept(SysDeptInsertDTO dto);

    /**
     * 修改部门
     */
    Mono<Void> updateDept(SysDeptUpdateDTO dto);

    /**
     * 删除部门
     */
    Mono<Void> deleteDeptById(Long deptId);

}
