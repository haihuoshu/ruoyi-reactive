package org.huanzhang.project.system.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色和菜单关联表 sys_role_menu
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Data
public class SysRoleMenu implements Serializable {

    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 菜单ID
     */
    private Long menuId;

}
