package org.huanzhang.project.system.service.impl;

import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.huanzhang.common.constant.UserConstants;
import org.huanzhang.common.core.text.Convert;
import org.huanzhang.common.exception.ServiceException;
import org.huanzhang.common.utils.SecurityUtils;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.project.system.converter.SysDeptMapper;
import org.huanzhang.project.system.dto.SysDeptInsertDTO;
import org.huanzhang.project.system.dto.SysDeptUpdateDTO;
import org.huanzhang.project.system.entity.SysDept;
import org.huanzhang.project.system.entity.SysUser;
import org.huanzhang.project.system.query.SysDeptQuery;
import org.huanzhang.project.system.repository.SysDeptRepository;
import org.huanzhang.project.system.service.SysDeptService;
import org.huanzhang.project.system.vo.SysDeptVO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Set;

/**
 * 部门表 业务处理
 *
 * @author haihuoshu
 * @version 2025-12-10
 */
@Service
public class SysDeptServiceImpl implements SysDeptService {

    @Resource
    private SysDeptRepository sysDeptRepository;
    @Resource
    private SysDeptMapper sysDeptMapper;

    /**
     * 根据条件查询部门列表
     */
    @Override
    public Flux<SysDeptVO> selectDeptList(SysDeptQuery query) {
        return sysDeptRepository.selectListByQuery(query)
                .map(sysDeptMapper::toVo);
    }

    /**
     * 查询部门列表（排除节点）
     */
    @Override
    public Flux<SysDeptVO> selectDeptListExclude(Long deptId) {
        return sysDeptRepository.selectListByQuery(new SysDeptQuery())
                .filter(d -> {
                    if (Objects.equals(deptId, d.getDeptId())) {
                        return false;
                    }
                    Set<String> set = org.springframework.util.StringUtils.commaDelimitedListToSet(d.getAncestors());
                    return BooleanUtils.isFalse(set.contains(deptId + ""));
                })
                .map(sysDeptMapper::toVo);
    }

    /**
     * 根据部门ID查询详细信息
     */
    @Override
    public Mono<SysDeptVO> selectDeptById(Long deptId) {
        return Mono.fromRunnable(() -> this.checkDeptDataScope(deptId))
                .then(sysDeptRepository.selectOneByDeptId(deptId))
                .switchIfEmpty(ServiceException.monoInstance("部门不存在"))
                .map(sysDeptMapper::toVo);
    }

    /**
     * 校验部门是否有数据权限
     */
    @Override
    public void checkDeptDataScope(Long deptId) {
        if (!SysUser.isAdmin(SecurityUtils.getUserId()) && StringUtils.isNotNull(deptId)) {
            SysDeptQuery query = new SysDeptQuery();
            query.setDeptId(deptId);
            sysDeptRepository.selectListByQuery(query)
                    .hasElements()
                    .flatMap(hasElements -> {
                        if (BooleanUtils.isFalse(hasElements)) {
                            return ServiceException.monoInstance("没有权限访问部门数据");
                        }
                        return Mono.empty();
                    })
                    .subscribe();
        }
    }

    /**
     * 新增部门
     */
    @Override
    public Mono<Void> insertDept(SysDeptInsertDTO dto) {
        SysDept entity = sysDeptMapper.toEntity(dto);

        return checkDeptNameUnique(entity)
                .then(sysDeptRepository.selectOneByDeptId(entity.getParentId()))
                .switchIfEmpty(ServiceException.monoInstance("上级部门不存在"))
                .flatMap(parent -> {
                    if (ObjectUtils.notEqual(parent.getStatus(), UserConstants.DEPT_NORMAL)) {
                        return ServiceException.monoInstance("上级部门已停用");
                    }

                    entity.setAncestors(parent.getAncestors() + "," + parent.getDeptId());
                    return sysDeptRepository.insertDept(entity);
                })
                .then();
    }

    /**
     * 检查部门名称是否唯一
     */
    private Mono<Void> checkDeptNameUnique(SysDept dept) {
        return sysDeptRepository.selectOneByParentIdAndDeptName(dept.getParentId(), dept.getDeptName())
                .flatMap(info -> {
                    if (ObjectUtils.notEqual(info.getDeptId(), dept.getDeptId())) {
                        return ServiceException.monoInstance("部门名称已存在");
                    }
                    return Mono.empty();
                })
                .then();
    }

    /**
     * 修改部门
     */
    @Override
    public Mono<Void> updateDept(SysDeptUpdateDTO dto) {
        SysDept entity = sysDeptMapper.toEntity(dto);

        return Mono.fromRunnable(() -> this.checkDeptDataScope(dto.getDeptId()))
                .then(checkDeptNameUnique(entity))
                .then(Mono.defer(() -> {
                    if (Objects.equals(dto.getDeptId(), dto.getParentId())) {
                        return ServiceException.monoInstance("上级部门不能是自己");
                    }
                    if (Objects.equals(dto.getStatus(), UserConstants.DEPT_DISABLE)) {
                        return sysDeptRepository.selectNormalChildrenDeptById(dto.getDeptId())
                                .flatMap(count -> {
                                    if (count > 0) {
                                        return ServiceException.monoInstance("包含未停用的子部门");
                                    }
                                    return Mono.empty();
                                });
                    }
                    return Mono.empty();
                }))
                .then(sysDeptRepository.selectOneByDeptId(dto.getDeptId()).switchIfEmpty(ServiceException.monoInstance("部门不存在")))
                .zipWith(sysDeptRepository.selectOneByDeptId(dto.getParentId()).switchIfEmpty(ServiceException.monoInstance("上级部门不存在")))
                .flatMap(tuple -> {
                    SysDept dept = tuple.getT1();
                    SysDept parent = tuple.getT2();

                    String newAncestors = parent.getAncestors() + "," + parent.getDeptId();
                    String oldAncestors = dept.getAncestors();
                    entity.setAncestors(newAncestors);
                    return sysDeptRepository.updateDept(entity)
                            .then(updateDeptParent(entity))
                            .then(updateDeptChildren(dto.getDeptId(), newAncestors, oldAncestors));
                })
                .then();
    }

    /**
     * 修改上级部门
     */
    private Mono<Long> updateDeptParent(SysDept dept) {
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus())) {
            String ancestors = dept.getAncestors();
            Long[] deptIds = Convert.toLongArray(ancestors);
            return sysDeptRepository.updateDeptStatusNormal(deptIds);
        }
        return Mono.empty();
    }

    /**
     * 修改下级部门
     */
    public Mono<Long> updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        return sysDeptRepository.selectChildrenByDeptId(deptId)
                .collectList()
                .flatMap(children -> {
                    if (CollectionUtils.isEmpty(children)) {
                        return Mono.empty();
                    }
                    for (SysDept child : children) {
                        child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
                    }
                    return sysDeptRepository.updateDeptChildren(children);
                });
    }

    /**
     * 删除部门
     */
    @Override
    public Mono<Void> deleteDeptById(Long deptId) {
        return Mono.fromRunnable(() -> this.checkDeptDataScope(deptId))
                .then(sysDeptRepository.existsChildByDeptId(deptId)
                        .flatMap(existsChild -> {
                            if (existsChild) {
                                return ServiceException.monoInstance("部门存在下级，不允许删除");
                            }
                            return Mono.empty();
                        })
                )
                .then(sysDeptRepository.existsUserByDeptId(deptId)
                        .flatMap(existsUser -> {
                            if (existsUser) {
                                return ServiceException.monoInstance("部门存在用户，不允许删除");
                            }
                            return Mono.empty();
                        })
                )
                .then(sysDeptRepository.deleteByDeptId(deptId))
                .then();
    }

}
