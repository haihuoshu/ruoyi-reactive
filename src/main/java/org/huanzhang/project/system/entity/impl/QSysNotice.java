package org.huanzhang.project.system.entity.impl;

import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import org.huanzhang.project.system.entity.SysNotice;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QSysNotice is a Querydsl query type for QSysNotice
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QSysNotice extends com.querydsl.sql.RelationalPathBase<SysNotice> {

    public final StringPath createBy = createString("createBy");

    public final DateTimePath<java.time.LocalDateTime> createTime = createDateTime("createTime", java.time.LocalDateTime.class);

    public final StringPath noticeContent = createString("noticeContent");

    public final NumberPath<Long> noticeId = createNumber("noticeId", Long.class);

    public final StringPath noticeTitle = createString("noticeTitle");

    public final StringPath noticeType = createString("noticeType");

    public final StringPath remark = createString("remark");

    public final StringPath status = createString("status");

    public final StringPath updateBy = createString("updateBy");

    public final DateTimePath<java.time.LocalDateTime> updateTime = createDateTime("updateTime", java.time.LocalDateTime.class);

    public final com.querydsl.sql.PrimaryKey<SysNotice> primary = createPrimaryKey(noticeId);

    public QSysNotice(String variable) {
        super(SysNotice.class, forVariable(variable), "null", "sys_notice");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(createBy, ColumnMetadata.named("create_by").withIndex(6).ofType(Types.VARCHAR).withSize(64));
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(7).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(noticeContent, ColumnMetadata.named("notice_content").withIndex(4).ofType(Types.LONGVARCHAR).withSize(2147483647));
        addMetadata(noticeId, ColumnMetadata.named("notice_id").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(noticeTitle, ColumnMetadata.named("notice_title").withIndex(2).ofType(Types.VARCHAR).withSize(50).notNull());
        addMetadata(noticeType, ColumnMetadata.named("notice_type").withIndex(3).ofType(Types.CHAR).withSize(1).notNull());
        addMetadata(remark, ColumnMetadata.named("remark").withIndex(10).ofType(Types.VARCHAR).withSize(255));
        addMetadata(status, ColumnMetadata.named("status").withIndex(5).ofType(Types.CHAR).withSize(1));
        addMetadata(updateBy, ColumnMetadata.named("update_by").withIndex(8).ofType(Types.VARCHAR).withSize(64));
        addMetadata(updateTime, ColumnMetadata.named("update_time").withIndex(9).ofType(Types.TIMESTAMP).withSize(19));
    }

}

