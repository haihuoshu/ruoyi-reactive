package org.huanzhang.project.system.query;

import lombok.Data;
import org.huanzhang.framework.web.domain.AbstractDataScope;
import org.huanzhang.framework.web.domain.PageQuery;

/**
 * 角色表 sys_role
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Data
public class SysRoleQuery extends PageQuery implements AbstractDataScope {

    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 角色权限
     */
    private String roleKey;
    /**
     * 角色状态（0正常 1停用）
     */
    private String status;
    /**
     * 数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限；5：仅本人数据权限）
     */
    private String dataScope;

}
