package org.huanzhang.project.system.entity.impl;

import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import org.huanzhang.project.system.entity.SysMenu;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QSysMenu is a Querydsl query type for QSysMenu
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QSysMenu extends com.querydsl.sql.RelationalPathBase<SysMenu> {

    public final StringPath component = createString("component");

    public final StringPath createBy = createString("createBy");

    public final DateTimePath<java.time.LocalDateTime> createTime = createDateTime("createTime", java.time.LocalDateTime.class);

    public final StringPath icon = createString("icon");

    public final NumberPath<Integer> isCache = createNumber("isCache", Integer.class);

    public final NumberPath<Integer> isFrame = createNumber("isFrame", Integer.class);

    public final NumberPath<Long> menuId = createNumber("menuId", Long.class);

    public final StringPath menuName = createString("menuName");

    public final StringPath menuType = createString("menuType");

    public final NumberPath<Integer> orderNum = createNumber("orderNum", Integer.class);

    public final NumberPath<Long> parentId = createNumber("parentId", Long.class);

    public final StringPath path = createString("path");

    public final StringPath perms = createString("perms");

    public final StringPath query = createString("query");

    public final StringPath remark = createString("remark");

    public final StringPath routeName = createString("routeName");

    public final StringPath status = createString("status");

    public final StringPath updateBy = createString("updateBy");

    public final DateTimePath<java.time.LocalDateTime> updateTime = createDateTime("updateTime", java.time.LocalDateTime.class);

    public final StringPath visible = createString("visible");

    public final com.querydsl.sql.PrimaryKey<SysMenu> primary = createPrimaryKey(menuId);

    public QSysMenu(String variable) {
        super(SysMenu.class, forVariable(variable), "null", "sys_menu");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(component, ColumnMetadata.named("component").withIndex(6).ofType(Types.VARCHAR).withSize(255));
        addMetadata(createBy, ColumnMetadata.named("create_by").withIndex(16).ofType(Types.VARCHAR).withSize(64));
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(17).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(icon, ColumnMetadata.named("icon").withIndex(15).ofType(Types.VARCHAR).withSize(100));
        addMetadata(isCache, ColumnMetadata.named("is_cache").withIndex(10).ofType(Types.INTEGER).withSize(10));
        addMetadata(isFrame, ColumnMetadata.named("is_frame").withIndex(9).ofType(Types.INTEGER).withSize(10));
        addMetadata(menuId, ColumnMetadata.named("menu_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(menuName, ColumnMetadata.named("menu_name").withIndex(2).ofType(Types.VARCHAR).withSize(50).notNull());
        addMetadata(menuType, ColumnMetadata.named("menu_type").withIndex(11).ofType(Types.CHAR).withSize(1));
        addMetadata(orderNum, ColumnMetadata.named("order_num").withIndex(4).ofType(Types.INTEGER).withSize(10));
        addMetadata(parentId, ColumnMetadata.named("parent_id").withIndex(3).ofType(Types.BIGINT).withSize(19));
        addMetadata(path, ColumnMetadata.named("path").withIndex(5).ofType(Types.VARCHAR).withSize(200));
        addMetadata(perms, ColumnMetadata.named("perms").withIndex(14).ofType(Types.VARCHAR).withSize(100));
        addMetadata(query, ColumnMetadata.named("query").withIndex(7).ofType(Types.VARCHAR).withSize(255));
        addMetadata(remark, ColumnMetadata.named("remark").withIndex(20).ofType(Types.VARCHAR).withSize(500));
        addMetadata(routeName, ColumnMetadata.named("route_name").withIndex(8).ofType(Types.VARCHAR).withSize(50));
        addMetadata(status, ColumnMetadata.named("status").withIndex(13).ofType(Types.CHAR).withSize(1));
        addMetadata(updateBy, ColumnMetadata.named("update_by").withIndex(18).ofType(Types.VARCHAR).withSize(64));
        addMetadata(updateTime, ColumnMetadata.named("update_time").withIndex(19).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(visible, ColumnMetadata.named("visible").withIndex(12).ofType(Types.CHAR).withSize(1));
    }

}

