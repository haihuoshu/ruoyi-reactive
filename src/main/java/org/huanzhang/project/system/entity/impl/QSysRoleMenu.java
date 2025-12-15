package org.huanzhang.project.system.entity.impl;

import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QSysRoleMenu is a Querydsl query type for QSysRoleMenu
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QSysRoleMenu extends com.querydsl.sql.RelationalPathBase<QSysRoleMenu> {

    public final NumberPath<Long> menuId = createNumber("menuId", Long.class);

    public final NumberPath<Long> roleId = createNumber("roleId", Long.class);

    public final com.querydsl.sql.PrimaryKey<QSysRoleMenu> primary = createPrimaryKey(menuId, roleId);

    public QSysRoleMenu(String variable) {
        super(QSysRoleMenu.class, forVariable(variable), "null", "sys_role_menu");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(menuId, ColumnMetadata.named("menu_id").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(roleId, ColumnMetadata.named("role_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

