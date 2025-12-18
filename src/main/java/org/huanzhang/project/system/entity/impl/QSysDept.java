package org.huanzhang.project.system.entity.impl;

import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import org.huanzhang.project.system.entity.SysDept;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QSysDept is a Querydsl query type for SysDept
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QSysDept extends com.querydsl.sql.RelationalPathBase<SysDept> {

    public final StringPath ancestors = createString("ancestors");

    public final StringPath createBy = createString("createBy");

    public final DateTimePath<java.time.LocalDateTime> createTime = createDateTime("createTime", java.time.LocalDateTime.class);

    public final StringPath delFlag = createString("delFlag");

    public final NumberPath<Long> deptId = createNumber("deptId", Long.class);

    public final StringPath deptName = createString("deptName");

    public final StringPath email = createString("email");

    public final StringPath leader = createString("leader");

    public final NumberPath<Integer> orderNum = createNumber("orderNum", Integer.class);

    public final NumberPath<Long> parentId = createNumber("parentId", Long.class);

    public final StringPath phone = createString("phone");

    public final StringPath status = createString("status");

    public final StringPath updateBy = createString("updateBy");

    public final DateTimePath<java.time.LocalDateTime> updateTime = createDateTime("updateTime", java.time.LocalDateTime.class);

    public final com.querydsl.sql.PrimaryKey<SysDept> primary = createPrimaryKey(deptId);

    public QSysDept(String variable) {
        super(SysDept.class, forVariable(variable), "null", "sys_dept");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(ancestors, ColumnMetadata.named("ancestors").withIndex(3).ofType(Types.VARCHAR).withSize(50));
        addMetadata(createBy, ColumnMetadata.named("create_by").withIndex(11).ofType(Types.VARCHAR).withSize(64));
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(12).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(delFlag, ColumnMetadata.named("del_flag").withIndex(10).ofType(Types.CHAR).withSize(1));
        addMetadata(deptId, ColumnMetadata.named("dept_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(deptName, ColumnMetadata.named("dept_name").withIndex(4).ofType(Types.VARCHAR).withSize(30));
        addMetadata(email, ColumnMetadata.named("email").withIndex(8).ofType(Types.VARCHAR).withSize(50));
        addMetadata(leader, ColumnMetadata.named("leader").withIndex(6).ofType(Types.VARCHAR).withSize(20));
        addMetadata(orderNum, ColumnMetadata.named("order_num").withIndex(5).ofType(Types.INTEGER).withSize(10));
        addMetadata(parentId, ColumnMetadata.named("parent_id").withIndex(2).ofType(Types.BIGINT).withSize(19));
        addMetadata(phone, ColumnMetadata.named("phone").withIndex(7).ofType(Types.VARCHAR).withSize(11));
        addMetadata(status, ColumnMetadata.named("status").withIndex(9).ofType(Types.CHAR).withSize(1));
        addMetadata(updateBy, ColumnMetadata.named("update_by").withIndex(13).ofType(Types.VARCHAR).withSize(64));
        addMetadata(updateTime, ColumnMetadata.named("update_time").withIndex(14).ofType(Types.TIMESTAMP).withSize(19));
    }

}

