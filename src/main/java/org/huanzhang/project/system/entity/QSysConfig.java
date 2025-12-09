package org.huanzhang.project.system.entity;

import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QSysConfig is a Querydsl query type for SysConfig
 */
@SuppressWarnings("ALL")
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QSysConfig extends com.querydsl.sql.RelationalPathBase<SysConfig> {

    private static final long serialVersionUID = -1492021304;

    public static final QSysConfig sysConfig = new QSysConfig("sys_config");

    public final NumberPath<Long> configId = createNumber("configId", Long.class);

    public final StringPath configKey = createString("configKey");

    public final StringPath configName = createString("configName");

    public final StringPath configType = createString("configType");

    public final StringPath configValue = createString("configValue");

    public final StringPath createBy = createString("createBy");

    public final DateTimePath<java.time.LocalDateTime> createTime = createDateTime("createTime", java.time.LocalDateTime.class);

    public final StringPath remark = createString("remark");

    public final StringPath updateBy = createString("updateBy");

    public final DateTimePath<java.time.LocalDateTime> updateTime = createDateTime("updateTime", java.time.LocalDateTime.class);

    public final com.querydsl.sql.PrimaryKey<SysConfig> primary = createPrimaryKey(configId);

    public QSysConfig(String variable) {
        super(SysConfig.class, forVariable(variable), "null", "sys_config");
        addMetadata();
    }

    public QSysConfig(String variable, String schema, String table) {
        super(SysConfig.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QSysConfig(String variable, String schema) {
        super(SysConfig.class, forVariable(variable), schema, "sys_config");
        addMetadata();
    }

    public QSysConfig(Path<? extends SysConfig> path) {
        super(path.getType(), path.getMetadata(), "null", "sys_config");
        addMetadata();
    }

    public QSysConfig(PathMetadata metadata) {
        super(SysConfig.class, metadata, "null", "sys_config");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(configId, ColumnMetadata.named("config_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(configKey, ColumnMetadata.named("config_key").withIndex(3).ofType(Types.VARCHAR).withSize(100));
        addMetadata(configName, ColumnMetadata.named("config_name").withIndex(2).ofType(Types.VARCHAR).withSize(100));
        addMetadata(configType, ColumnMetadata.named("config_type").withIndex(5).ofType(Types.CHAR).withSize(1));
        addMetadata(configValue, ColumnMetadata.named("config_value").withIndex(4).ofType(Types.VARCHAR).withSize(500));
        addMetadata(createBy, ColumnMetadata.named("create_by").withIndex(6).ofType(Types.VARCHAR).withSize(64));
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(7).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(remark, ColumnMetadata.named("remark").withIndex(10).ofType(Types.VARCHAR).withSize(500));
        addMetadata(updateBy, ColumnMetadata.named("update_by").withIndex(8).ofType(Types.VARCHAR).withSize(64));
        addMetadata(updateTime, ColumnMetadata.named("update_time").withIndex(9).ofType(Types.TIMESTAMP).withSize(19));
    }

}

