package org.huanzhang.project.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.huanzhang.framework.aspectj.lang.annotation.Excel;
import org.huanzhang.framework.aspectj.lang.annotation.Excel.ColumnType;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Schema(description = "SysRoleVO")
@Data
public class SysRoleVO implements Serializable {

    @Schema(description = "角色ID")
    @Excel(name = "角色序号", cellType = ColumnType.NUMERIC)
    private Long roleId;

    @Schema(description = "角色名称")
    @Excel(name = "角色名称")
    private String roleName;

    @Schema(description = "角色权限")
    @Excel(name = "角色权限")
    private String roleKey;

    @Schema(description = "角色排序")
    @Excel(name = "角色排序")
    private Integer roleSort;

    @Schema(description = "数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限；5：仅本人数据权限）")
    @Excel(name = "数据范围", readConverterExp = "1=所有数据权限,2=自定义数据权限,3=本部门数据权限,4=本部门及以下数据权限,5=仅本人数据权限")
    private String dataScope;

    @Schema(description = "菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）")
    private boolean menuCheckStrictly;

    @Schema(description = "部门树选择项是否关联显示（0：父子不互相关联显示 1：父子互相关联显示 ）")
    private boolean deptCheckStrictly;

    @Schema(description = "角色状态（0正常 1停用）")
    @Excel(name = "角色状态", readConverterExp = "0=正常,1=停用")
    private String status;

    @Schema(description = "菜单组")
    private List<Long> menuIds;

    @Schema(description = "部门组（数据权限）")
    private List<Long> deptIds;

    @Schema(description = "角色菜单权限")
    private Set<String> permissions;

}
