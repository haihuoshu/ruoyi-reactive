package org.huanzhang.project.system.entity.impl;

import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import org.huanzhang.project.system.entity.SysAccessLog;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QSysLogininfor is a Querydsl query type for QSysLogininfor
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QSysAccessLog extends com.querydsl.sql.RelationalPathBase<SysAccessLog> {

    public final StringPath browser = createString("browser");

    public final NumberPath<Long> infoId = createNumber("infoId", Long.class);

    public final StringPath ipaddr = createString("ipaddr");

    public final StringPath loginLocation = createString("loginLocation");

    public final DateTimePath<java.time.LocalDateTime> loginTime = createDateTime("loginTime", java.time.LocalDateTime.class);

    public final StringPath msg = createString("msg");

    public final StringPath os = createString("os");

    public final StringPath status = createString("status");

    public final StringPath userName = createString("userName");

    public final com.querydsl.sql.PrimaryKey<SysAccessLog> primary = createPrimaryKey(infoId);

    public QSysAccessLog(String variable) {
        super(SysAccessLog.class, forVariable(variable), "null", "sys_logininfor");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(browser, ColumnMetadata.named("browser").withIndex(5).ofType(Types.VARCHAR).withSize(50));
        addMetadata(infoId, ColumnMetadata.named("info_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(ipaddr, ColumnMetadata.named("ipaddr").withIndex(3).ofType(Types.VARCHAR).withSize(128));
        addMetadata(loginLocation, ColumnMetadata.named("login_location").withIndex(4).ofType(Types.VARCHAR).withSize(255));
        addMetadata(loginTime, ColumnMetadata.named("login_time").withIndex(9).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(msg, ColumnMetadata.named("msg").withIndex(8).ofType(Types.VARCHAR).withSize(255));
        addMetadata(os, ColumnMetadata.named("os").withIndex(6).ofType(Types.VARCHAR).withSize(50));
        addMetadata(status, ColumnMetadata.named("status").withIndex(7).ofType(Types.CHAR).withSize(1));
        addMetadata(userName, ColumnMetadata.named("user_name").withIndex(2).ofType(Types.VARCHAR).withSize(50));
    }

}

