package org.huanzhang.project.system.entity.impl;

import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import org.huanzhang.project.system.entity.SysUserPost;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QSysUserPost is a Querydsl query type for QSysUserPost
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QSysUserPost extends com.querydsl.sql.RelationalPathBase<SysUserPost> {

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.querydsl.sql.PrimaryKey<SysUserPost> primary = createPrimaryKey(postId, userId);

    public QSysUserPost(String variable) {
        super(SysUserPost.class, forVariable(variable), "null", "sys_user_post");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(postId, ColumnMetadata.named("post_id").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

