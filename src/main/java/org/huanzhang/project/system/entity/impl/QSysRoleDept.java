package org.huanzhang.project.system.entity.impl;

import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import org.huanzhang.project.system.entity.SysRoleDept;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QSysRoleDept is a Querydsl query type for QSysRoleDept
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QSysRoleDept extends com.querydsl.sql.RelationalPathBase<SysRoleDept> {

    public final NumberPath<Long> deptId = createNumber("deptId", Long.class);

    public final NumberPath<Long> roleId = createNumber("roleId", Long.class);

    public final com.querydsl.sql.PrimaryKey<SysRoleDept> primary = createPrimaryKey(deptId, roleId);

    public QSysRoleDept(String variable) {
        super(SysRoleDept.class, forVariable(variable), "null", "sys_role_dept");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(deptId, ColumnMetadata.named("dept_id").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(roleId, ColumnMetadata.named("role_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

