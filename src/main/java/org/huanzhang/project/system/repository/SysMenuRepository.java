package org.huanzhang.project.system.repository;

import org.huanzhang.framework.r2dbc.repository.AuditableRepository;
import org.huanzhang.project.system.entity.SysMenu;
import org.huanzhang.project.system.query.SysMenuQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 菜单表 数据层
 *
 * @author haihuoshu
 * @version 2025-12-12
 */
public interface SysMenuRepository extends AuditableRepository<SysMenu> {

    /**
     * 根据条件查询菜单列表
     */
    Flux<SysMenu> selectListByQuery(SysMenuQuery query);

    /**
     * 根据用户查询菜单列表
     */
    Flux<SysMenu> selectListByUserId(Long userId, SysMenuQuery query);

    /**
     * 根据菜单ID查询
     */
    Mono<SysMenu> selectOneById(Long menuId);

    /**
     * 根据上级菜单ID和菜单名称查询
     */
    Mono<SysMenu> selectOneByParentIdAndMenuName(Long parentId, String menuName);

    /**
     * 新增菜单
     */
    Mono<Long> insertMenu(SysMenu menu);

    /**
     * 修改菜单
     */
    Mono<Long> updateMenu(SysMenu menu);

    /**
     * 是否存在菜单子节点
     */
    Mono<Long> selectCountByParentId(Long parentId);

    /**
     * 根据菜单ID删除
     */
    Mono<Long> deleteByMenuId(Long menuId);

    /**
     * 根据用户ID查询菜单
     */
    Flux<SysMenu> selectMenuTreeByUserId(Long userId);

}
