package org.huanzhang.project.system.entity.impl;

import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import org.huanzhang.project.system.entity.SysOperateLog;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QSysOperLog is a Querydsl query type for QSysOperLog
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QSysOperateLog extends com.querydsl.sql.RelationalPathBase<SysOperateLog> {

    public final NumberPath<Integer> businessType = createNumber("businessType", Integer.class);

    public final NumberPath<Long> costTime = createNumber("costTime", Long.class);

    public final StringPath deptName = createString("deptName");

    public final StringPath errorMsg = createString("errorMsg");

    public final StringPath jsonResult = createString("jsonResult");

    public final StringPath method = createString("method");

    public final NumberPath<Integer> operatorType = createNumber("operatorType", Integer.class);

    public final NumberPath<Long> operId = createNumber("operId", Long.class);

    public final StringPath operIp = createString("operIp");

    public final StringPath operLocation = createString("operLocation");

    public final StringPath operName = createString("operName");

    public final StringPath operParam = createString("operParam");

    public final DateTimePath<java.time.LocalDateTime> operTime = createDateTime("operTime", java.time.LocalDateTime.class);

    public final StringPath operUrl = createString("operUrl");

    public final StringPath requestMethod = createString("requestMethod");

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    public final StringPath title = createString("title");

    public final com.querydsl.sql.PrimaryKey<SysOperateLog> primary = createPrimaryKey(operId);

    public QSysOperateLog(String variable) {
        super(SysOperateLog.class, forVariable(variable), "null", "sys_oper_log");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(businessType, ColumnMetadata.named("business_type").withIndex(3).ofType(Types.INTEGER).withSize(10));
        addMetadata(costTime, ColumnMetadata.named("cost_time").withIndex(17).ofType(Types.BIGINT).withSize(19));
        addMetadata(deptName, ColumnMetadata.named("dept_name").withIndex(8).ofType(Types.VARCHAR).withSize(50));
        addMetadata(errorMsg, ColumnMetadata.named("error_msg").withIndex(15).ofType(Types.VARCHAR).withSize(2000));
        addMetadata(jsonResult, ColumnMetadata.named("json_result").withIndex(13).ofType(Types.VARCHAR).withSize(2000));
        addMetadata(method, ColumnMetadata.named("method").withIndex(4).ofType(Types.VARCHAR).withSize(200));
        addMetadata(operatorType, ColumnMetadata.named("operator_type").withIndex(6).ofType(Types.INTEGER).withSize(10));
        addMetadata(operId, ColumnMetadata.named("oper_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(operIp, ColumnMetadata.named("oper_ip").withIndex(10).ofType(Types.VARCHAR).withSize(128));
        addMetadata(operLocation, ColumnMetadata.named("oper_location").withIndex(11).ofType(Types.VARCHAR).withSize(255));
        addMetadata(operName, ColumnMetadata.named("oper_name").withIndex(7).ofType(Types.VARCHAR).withSize(50));
        addMetadata(operParam, ColumnMetadata.named("oper_param").withIndex(12).ofType(Types.VARCHAR).withSize(2000));
        addMetadata(operTime, ColumnMetadata.named("oper_time").withIndex(16).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(operUrl, ColumnMetadata.named("oper_url").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(requestMethod, ColumnMetadata.named("request_method").withIndex(5).ofType(Types.VARCHAR).withSize(10));
        addMetadata(status, ColumnMetadata.named("status").withIndex(14).ofType(Types.INTEGER).withSize(10));
        addMetadata(title, ColumnMetadata.named("title").withIndex(2).ofType(Types.VARCHAR).withSize(50));
    }

}

