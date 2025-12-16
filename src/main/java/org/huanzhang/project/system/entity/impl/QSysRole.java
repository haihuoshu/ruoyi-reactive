package org.huanzhang.project.system.entity.impl;

import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import org.huanzhang.project.system.entity.SysRole;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QSysRole is a Querydsl query type for QSysRole
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QSysRole extends com.querydsl.sql.RelationalPathBase<SysRole> {

    public final StringPath createBy = createString("createBy");

    public final DateTimePath<java.time.LocalDateTime> createTime = createDateTime("createTime", java.time.LocalDateTime.class);

    public final StringPath dataScope = createString("dataScope");

    public final StringPath delFlag = createString("delFlag");

    public final BooleanPath deptCheckStrictly = createBoolean("deptCheckStrictly");

    public final BooleanPath menuCheckStrictly = createBoolean("menuCheckStrictly");

    public final StringPath remark = createString("remark");

    public final NumberPath<Long> roleId = createNumber("roleId", Long.class);

    public final StringPath roleKey = createString("roleKey");

    public final StringPath roleName = createString("roleName");

    public final NumberPath<Integer> roleSort = createNumber("roleSort", Integer.class);

    public final StringPath status = createString("status");

    public final StringPath updateBy = createString("updateBy");

    public final DateTimePath<java.time.LocalDateTime> updateTime = createDateTime("updateTime", java.time.LocalDateTime.class);

    public final com.querydsl.sql.PrimaryKey<SysRole> primary = createPrimaryKey(roleId);

    public QSysRole(String variable) {
        super(SysRole.class, forVariable(variable), "null", "sys_role");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(createBy, ColumnMetadata.named("create_by").withIndex(10).ofType(Types.VARCHAR).withSize(64));
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(11).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(dataScope, ColumnMetadata.named("data_scope").withIndex(5).ofType(Types.CHAR).withSize(1));
        addMetadata(delFlag, ColumnMetadata.named("del_flag").withIndex(9).ofType(Types.CHAR).withSize(1));
        addMetadata(deptCheckStrictly, ColumnMetadata.named("dept_check_strictly").withIndex(7).ofType(Types.BIT).withSize(1));
        addMetadata(menuCheckStrictly, ColumnMetadata.named("menu_check_strictly").withIndex(6).ofType(Types.BIT).withSize(1));
        addMetadata(remark, ColumnMetadata.named("remark").withIndex(14).ofType(Types.VARCHAR).withSize(500));
        addMetadata(roleId, ColumnMetadata.named("role_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(roleKey, ColumnMetadata.named("role_key").withIndex(3).ofType(Types.VARCHAR).withSize(100).notNull());
        addMetadata(roleName, ColumnMetadata.named("role_name").withIndex(2).ofType(Types.VARCHAR).withSize(30).notNull());
        addMetadata(roleSort, ColumnMetadata.named("role_sort").withIndex(4).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(status, ColumnMetadata.named("status").withIndex(8).ofType(Types.CHAR).withSize(1).notNull());
        addMetadata(updateBy, ColumnMetadata.named("update_by").withIndex(12).ofType(Types.VARCHAR).withSize(64));
        addMetadata(updateTime, ColumnMetadata.named("update_time").withIndex(13).ofType(Types.TIMESTAMP).withSize(19));
    }

}

