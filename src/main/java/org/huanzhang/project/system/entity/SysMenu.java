package org.huanzhang.project.system.entity;

import lombok.Data;
import org.huanzhang.framework.r2dbc.entity.AbstractAuditable;

/**
 * 菜单表 sys_menu
 *
 * @author haihuoshu
 * @version 2025-12-12
 */
@Data
public class SysMenu extends AbstractAuditable {

    /**
     * 菜单ID
     */
    private Long menuId;
    /**
     * 上级菜单ID
     */
    private Long parentId;
    /**
     * 菜单名称
     */
    private String menuName;
    /**
     * 显示顺序
     */
    private Integer orderNum;
    /**
     * 路由地址
     */
    private String path;
    /**
     * 组件路径
     */
    private String component;
    /**
     * 路由参数
     */
    private String query;
    /**
     * 路由名称，默认和路由地址相同的驼峰格式（注意：因为vue3版本的router会删除名称相同路由，为避免名字的冲突，特殊情况可以自定义）
     */
    private String routeName;
    /**
     * 是否为外链（0是 1否）
     */
    private Integer isFrame;
    /**
     * 是否缓存（0缓存 1不缓存）
     */
    private Integer isCache;
    /**
     * 类型（M目录 C菜单 F按钮）
     */
    private String menuType;
    /**
     * 显示状态（0显示 1隐藏）
     */
    private String visible;
    /**
     * 菜单状态（0正常 1停用）
     */
    private String status;
    /**
     * 权限字符串
     */
    private String perms;
    /**
     * 菜单图标
     */
    private String icon;
    /**
     * 备注
     */
    private String remark;

}
