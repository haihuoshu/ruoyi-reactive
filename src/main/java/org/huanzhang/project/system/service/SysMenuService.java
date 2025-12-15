package org.huanzhang.project.system.service;

import org.huanzhang.project.system.domain.vo.RouterVo;
import org.huanzhang.project.system.dto.SysMenuInsertDTO;
import org.huanzhang.project.system.dto.SysMenuUpdateDTO;
import org.huanzhang.project.system.query.SysMenuQuery;
import org.huanzhang.project.system.vo.SysMenuVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

/**
 * 菜单表 业务层
 *
 * @author haihuoshu
 * @version 2025-12-12
 */
public interface SysMenuService {

    /**
     * 根据条件查询菜单列表
     */
    Flux<SysMenuVO> selectMenuList(SysMenuQuery query);

    /**
     * 根据菜单ID查询详细信息
     */
    Mono<SysMenuVO> selectMenuById(Long menuId);

    /**
     * 新增菜单
     */
    Mono<Void> insertMenu(SysMenuInsertDTO dto);

    /**
     * 修改菜单
     */
    Mono<Void> updateMenu(SysMenuUpdateDTO dto);

    /**
     * 删除菜单
     */
    Mono<Void> deleteMenuById(Long menuId);

    /**
     * 根据用户ID查询权限
     */
    Mono<Set<String>> selectMenuPermsByUserId(Long userId);

    /**
     * 根据用户ID查询菜单树信息
     */
    Mono<List<SysMenuVO>> selectMenuTreeByUserId(Long userId);

    /**
     * 构建前端路由所需要的菜单
     */
    List<RouterVo> buildMenus(List<SysMenuVO> menus);

}
