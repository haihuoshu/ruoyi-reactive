package org.huanzhang.project.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.huanzhang.common.constant.Constants;
import org.huanzhang.common.constant.UserConstants;
import org.huanzhang.common.exception.ServiceException;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.framework.security.ReactiveSecurityUtils;
import org.huanzhang.framework.web.tree.TreeUtils;
import org.huanzhang.project.system.converter.SysMenuMapper;
import org.huanzhang.project.system.domain.vo.MetaVo;
import org.huanzhang.project.system.domain.vo.RouterVo;
import org.huanzhang.project.system.dto.SysMenuInsertDTO;
import org.huanzhang.project.system.dto.SysMenuUpdateDTO;
import org.huanzhang.project.system.entity.SysMenu;
import org.huanzhang.project.system.query.SysMenuQuery;
import org.huanzhang.project.system.repository.SysMenuRepository;
import org.huanzhang.project.system.repository.SysRoleMenuRepository;
import org.huanzhang.project.system.service.SysMenuService;
import org.huanzhang.project.system.vo.SysMenuVO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * 菜单表 业务处理
 *
 * @author haihuoshu
 * @version 2025-12-12
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuRepository sysMenuRepository;
    private final SysMenuMapper sysMenuMapper;

    private final SysRoleMenuRepository sysRoleMenuRepository;

    /**
     * 根据条件查询菜单列表
     */
    @Override
    public Flux<SysMenuVO> selectMenuList(SysMenuQuery query) {
        return ReactiveSecurityUtils.getUserId()
                .flatMapMany(userId -> {
                    // 管理员显示所有菜单信息
                    if (ReactiveSecurityUtils.isAdmin(userId)) {
                        return sysMenuRepository.selectListByQuery(query);
                    } else {
                        return sysMenuRepository.selectListByUserId(userId, query);
                    }
                })
                .map(sysMenuMapper::toVo);
    }

    /**
     * 根据菜单ID查询详细信息
     */
    @Override
    public Mono<SysMenuVO> selectMenuById(Long menuId) {
        return sysMenuRepository.selectOneById(menuId)
                .switchIfEmpty(ServiceException.monoInstance("菜单不存在"))
                .map(sysMenuMapper::toVo);
    }

    /**
     * 新增菜单
     */
    @Override
    public Mono<Void> insertMenu(SysMenuInsertDTO dto) {
        SysMenu entity = sysMenuMapper.toEntity(dto);

        return checkMenuNameUnique(entity)
                .then(sysMenuRepository.insertMenu(entity))
                .then();
    }

    /**
     * 检查菜单名称是否唯一
     */
    private Mono<Void> checkMenuNameUnique(SysMenu menu) {
        return sysMenuRepository.selectOneByParentIdAndMenuName(menu.getParentId(), menu.getMenuName())
                .flatMap(info -> {
                    if (ObjectUtils.notEqual(info.getMenuId(), menu.getMenuId())) {
                        return ServiceException.monoInstance("菜单名称已存在");
                    }
                    return Mono.empty();
                })
                .then(Mono.defer(() -> {
                    if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
                        return ServiceException.monoInstance("地址必须以http(s)://开头");
                    }
                    if (Objects.equals(menu.getParentId(), menu.getMenuId())) {
                        return ServiceException.monoInstance("上级菜单不能选择自己");
                    }
                    return Mono.empty();
                }))
                .then();
    }

    /**
     * 修改菜单
     */
    @Override
    public Mono<Void> updateMenu(SysMenuUpdateDTO dto) {
        SysMenu entity = sysMenuMapper.toEntity(dto);

        return checkMenuNameUnique(entity)
                .then(sysMenuRepository.selectOneById(dto.getMenuId()))
                .switchIfEmpty(ServiceException.monoInstance("菜单不存在"))
                .then(sysMenuRepository.updateMenu(entity))
                .then();
    }

    /**
     * 删除菜单
     */
    @Override
    public Mono<Void> deleteMenuById(Long menuId) {
        return sysMenuRepository.selectCountByParentId(menuId)
                .flatMap(count -> {
                    if (count > 0) {
                        return ServiceException.monoInstance("存在下级，不允许删除");
                    }
                    return Mono.empty();
                })
                .then(sysRoleMenuRepository.selectCountByMenuId(menuId))
                .flatMap(count -> {
                    if (count > 0) {
                        return ServiceException.monoInstance("菜单已分配，不允许删除");
                    }
                    return Mono.empty();
                })
                .then(sysMenuRepository.deleteByMenuId(menuId))
                .then();
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Mono<Set<String>> selectMenuPermsByUserId(Long userId) {
        SysMenuQuery query = new SysMenuQuery();
        query.setStatus(UserConstants.NORMAL);

        return sysMenuRepository.selectListByUserId(userId, query)
                .filter(menu -> StringUtils.isNotEmpty(menu.getPerms()))
                .flatMap(menu -> {
                    String perms = menu.getPerms().trim();
                    // 按逗号分割后转换为Flux<String>
                    return Flux.fromArray(perms.split(","));
                })
                .collect(HashSet::new, Set::add);
    }

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户名称
     * @return 菜单列表
     */
    @Override
    public Mono<List<SysMenuVO>> selectMenuTreeByUserId(Long userId) {
        return sysMenuRepository.selectMenuTreeByUserId(userId)
                .map(sysMenuMapper::toVo)
                .collectList()
                .map(TreeUtils::getTree);
    }

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenuVO> menus) {
        List<RouterVo> routers = new LinkedList<>();
        for (SysMenuVO menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), Objects.equals(1, menu.getIsCache()), menu.getPath()));
            List<SysMenuVO> cMenus = menu.getChildren();
            if (StringUtils.isNotEmpty(cMenus) && UserConstants.TYPE_DIR.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(getRouteName(menu.getRouteName(), menu.getPath()));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), Objects.equals(1, menu.getIsCache()), menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId().intValue() == 0 && isInnerLink(menu)) {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(getRouteName(menu.getRouteName(), routerPath));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(SysMenuVO menu) {
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            return StringUtils.EMPTY;
        }
        return getRouteName(menu.getRouteName(), menu.getPath());
    }

    /**
     * 获取路由名称，如没有配置路由名称则取路由地址
     *
     * @param name 路由名称
     * @param path 路由地址
     * @return 路由名称（驼峰格式）
     */
    public String getRouteName(String name, String path) {
        String routerName = StringUtils.isNotEmpty(name) ? name : path;
        return StringUtils.capitalize(routerName);
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenuVO menu) {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId().intValue() && UserConstants.TYPE_DIR.equals(menu.getMenuType()) && UserConstants.NO_FRAME.equals(menu.getIsFrame())) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(SysMenuVO menu) {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            component = UserConstants.INNER_LINK;
        } else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMenuFrame(SysMenuVO menu) {
        return menu.getParentId().intValue() == 0 && UserConstants.TYPE_MENU.equals(menu.getMenuType())
                && menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }

    /**
     * 是否为parent_view组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isParentView(SysMenuVO menu) {
        return menu.getParentId().intValue() != 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType());
    }

    /**
     * 是否为内链组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isInnerLink(SysMenuVO menu) {
        return menu.getIsFrame().equals(UserConstants.NO_FRAME) && StringUtils.ishttp(menu.getPath());
    }

    /**
     * 内链域名特殊字符替换
     *
     * @return 替换后的内链域名
     */
    public String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(path, new String[]{Constants.HTTP, Constants.HTTPS, Constants.WWW, ".", ":"},
                new String[]{"", "", "", "/", "/"});
    }
}
