package org.huanzhang.project.system.repository;

import reactor.core.publisher.Mono;

/**
 * 角色与菜单关联表 数据层
 *
 * @author haihuoshu
 * @version 2025-12-12
 */
public interface SysRoleMenuRepository {

    Mono<Long> selectCountByMenuId(Long menuId);

}
