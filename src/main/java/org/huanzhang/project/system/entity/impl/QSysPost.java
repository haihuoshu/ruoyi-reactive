package org.huanzhang.project.system.entity.impl;

import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import org.huanzhang.project.system.entity.SysPost;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QSysPost is a Querydsl query type for QSysPost
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QSysPost extends com.querydsl.sql.RelationalPathBase<SysPost> {

    public final StringPath createBy = createString("createBy");

    public final DateTimePath<java.time.LocalDateTime> createTime = createDateTime("createTime", java.time.LocalDateTime.class);

    public final StringPath postCode = createString("postCode");

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final StringPath postName = createString("postName");

    public final NumberPath<Integer> postSort = createNumber("postSort", Integer.class);

    public final StringPath remark = createString("remark");

    public final StringPath status = createString("status");

    public final StringPath updateBy = createString("updateBy");

    public final DateTimePath<java.time.LocalDateTime> updateTime = createDateTime("updateTime", java.time.LocalDateTime.class);

    public final com.querydsl.sql.PrimaryKey<SysPost> primary = createPrimaryKey(postId);

    public QSysPost(String variable) {
        super(SysPost.class, forVariable(variable), "null", "sys_post");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(createBy, ColumnMetadata.named("create_by").withIndex(6).ofType(Types.VARCHAR).withSize(64));
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(7).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(postCode, ColumnMetadata.named("post_code").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(postId, ColumnMetadata.named("post_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(postName, ColumnMetadata.named("post_name").withIndex(3).ofType(Types.VARCHAR).withSize(50).notNull());
        addMetadata(postSort, ColumnMetadata.named("post_sort").withIndex(4).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(remark, ColumnMetadata.named("remark").withIndex(10).ofType(Types.VARCHAR).withSize(500));
        addMetadata(status, ColumnMetadata.named("status").withIndex(5).ofType(Types.CHAR).withSize(1).notNull());
        addMetadata(updateBy, ColumnMetadata.named("update_by").withIndex(8).ofType(Types.VARCHAR).withSize(64));
        addMetadata(updateTime, ColumnMetadata.named("update_time").withIndex(9).ofType(Types.TIMESTAMP).withSize(19));
    }

}

