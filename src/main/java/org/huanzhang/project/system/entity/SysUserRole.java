package org.huanzhang.project.system.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户和角色关联表 sys_user_role
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Data
public class SysUserRole implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 角色ID
     */
    private Long roleId;

}
