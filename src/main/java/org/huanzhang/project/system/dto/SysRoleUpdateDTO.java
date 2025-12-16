package org.huanzhang.project.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Schema(description = "SysRoleUpdateDTO")
@Data
public class SysRoleUpdateDTO implements Serializable {

    @Schema(description = "角色ID")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "角色名称")
    @Size(max = 30, message = "角色名称长度不能超过30个字符")
    private String roleName;

    @Schema(description = "角色权限")
    @Size(max = 100, message = "权限字符长度不能超过100个字符")
    private String roleKey;

    @Schema(description = "角色排序")
    private Integer roleSort;

    @Schema(description = "数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限；5：仅本人数据权限）")
    private String dataScope;

    @Schema(description = "菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）")
    private boolean menuCheckStrictly;

    @Schema(description = "部门树选择项是否关联显示（0：父子不互相关联显示 1：父子互相关联显示 ）")
    private boolean deptCheckStrictly;

    @Schema(description = "角色状态（0正常 1停用）")
    private String status;

    @Schema(description = "菜单组")
    private List<Long> menuIds;

    @Schema(description = "部门组（数据权限）")
    private List<Long> deptIds;

    @Schema(description = "备注")
    @Size(max = 50, message = "备注不能超过500个字符")
    private String remark;

}
