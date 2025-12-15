package org.huanzhang.project.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "SysMenuInsertDTO")
@Data
public class SysMenuInsertDTO implements Serializable {

    @Schema(description = "上级菜单ID")
    @NotNull(message = "上级菜单不能为空")
    private Long parentId;

    @Schema(description = "菜单名称")
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    @Schema(description = "显示顺序")
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    @Schema(description = "路由地址")
    @Size(max = 200, message = "路由地址不能超过200个字符")
    private String path;

    @Schema(description = "组件路径")
    @Size(max = 200, message = "组件路径不能超过255个字符")
    private String component;

    @Schema(description = "路由参数")
    private String query;

    @Schema(description = "路由名称，默认和路由地址相同的驼峰格式（注意：因为vue3版本的router会删除名称相同路由，为避免名字的冲突，特殊情况可以自定义）")
    private String routeName;

    @Schema(description = "是否为外链（0是 1否）")
    private Integer isFrame;

    @Schema(description = "是否缓存（0缓存 1不缓存）")
    private Integer isCache;

    @Schema(description = "类型（M目录 C菜单 F按钮）")
    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    @Schema(description = "显示状态（0显示 1隐藏）")
    @NotBlank(message = "显示状态不能为空")
    private String visible;

    @Schema(description = "菜单状态（0正常 1停用）")
    @NotBlank(message = "菜单状态不能为空")
    private String status;

    @Schema(description = "权限字符串")
    @Size(max = 100, message = "权限标识不能超过100个字符")
    private String perms;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;

}
