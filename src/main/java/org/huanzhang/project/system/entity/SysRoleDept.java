package org.huanzhang.project.system.entity;

import com.querydsl.core.annotations.Generated;

/**
 * SysRoleDept is a Querydsl bean type
 */
@SuppressWarnings("ALL")
@Generated("com.querydsl.codegen.BeanSerializer")
public class SysRoleDept {

    private Long deptId;

    private Long roleId;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

}

