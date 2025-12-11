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
 * QSysUser is a Querydsl query type for SysUser
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QSysUser extends com.querydsl.sql.RelationalPathBase<SysUser> {

    private static final long serialVersionUID = -1878105647;

    public static final QSysUser sysUser = new QSysUser("sys_user");

    public final StringPath avatar = createString("avatar");

    public final StringPath createBy = createString("createBy");

    public final DateTimePath<java.time.LocalDateTime> createTime = createDateTime("createTime", java.time.LocalDateTime.class);

    public final StringPath delFlag = createString("delFlag");

    public final NumberPath<Long> deptId = createNumber("deptId", Long.class);

    public final StringPath email = createString("email");

    public final DateTimePath<java.time.LocalDateTime> loginDate = createDateTime("loginDate", java.time.LocalDateTime.class);

    public final StringPath loginIp = createString("loginIp");

    public final StringPath nickName = createString("nickName");

    public final StringPath password = createString("password");

    public final StringPath phonenumber = createString("phonenumber");

    public final DateTimePath<java.time.LocalDateTime> pwdUpdateDate = createDateTime("pwdUpdateDate", java.time.LocalDateTime.class);

    public final StringPath remark = createString("remark");

    public final StringPath sex = createString("sex");

    public final StringPath status = createString("status");

    public final StringPath updateBy = createString("updateBy");

    public final DateTimePath<java.time.LocalDateTime> updateTime = createDateTime("updateTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath userName = createString("userName");

    public final StringPath userType = createString("userType");

    public final com.querydsl.sql.PrimaryKey<SysUser> primary = createPrimaryKey(userId);

    public QSysUser(String variable) {
        super(SysUser.class, forVariable(variable), "null", "sys_user");
        addMetadata();
    }

    public QSysUser(String variable, String schema, String table) {
        super(SysUser.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QSysUser(String variable, String schema) {
        super(SysUser.class, forVariable(variable), schema, "sys_user");
        addMetadata();
    }

    public QSysUser(Path<? extends SysUser> path) {
        super(path.getType(), path.getMetadata(), "null", "sys_user");
        addMetadata();
    }

    public QSysUser(PathMetadata metadata) {
        super(SysUser.class, metadata, "null", "sys_user");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(avatar, ColumnMetadata.named("avatar").withIndex(9).ofType(Types.VARCHAR).withSize(100));
        addMetadata(createBy, ColumnMetadata.named("create_by").withIndex(16).ofType(Types.VARCHAR).withSize(64));
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(17).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(delFlag, ColumnMetadata.named("del_flag").withIndex(12).ofType(Types.CHAR).withSize(1));
        addMetadata(deptId, ColumnMetadata.named("dept_id").withIndex(2).ofType(Types.BIGINT).withSize(19));
        addMetadata(email, ColumnMetadata.named("email").withIndex(6).ofType(Types.VARCHAR).withSize(50));
        addMetadata(loginDate, ColumnMetadata.named("login_date").withIndex(14).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(loginIp, ColumnMetadata.named("login_ip").withIndex(13).ofType(Types.VARCHAR).withSize(128));
        addMetadata(nickName, ColumnMetadata.named("nick_name").withIndex(4).ofType(Types.VARCHAR).withSize(30).notNull());
        addMetadata(password, ColumnMetadata.named("password").withIndex(10).ofType(Types.VARCHAR).withSize(100));
        addMetadata(phonenumber, ColumnMetadata.named("phonenumber").withIndex(7).ofType(Types.VARCHAR).withSize(11));
        addMetadata(pwdUpdateDate, ColumnMetadata.named("pwd_update_date").withIndex(15).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(remark, ColumnMetadata.named("remark").withIndex(20).ofType(Types.VARCHAR).withSize(500));
        addMetadata(sex, ColumnMetadata.named("sex").withIndex(8).ofType(Types.CHAR).withSize(1));
        addMetadata(status, ColumnMetadata.named("status").withIndex(11).ofType(Types.CHAR).withSize(1));
        addMetadata(updateBy, ColumnMetadata.named("update_by").withIndex(18).ofType(Types.VARCHAR).withSize(64));
        addMetadata(updateTime, ColumnMetadata.named("update_time").withIndex(19).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(userName, ColumnMetadata.named("user_name").withIndex(3).ofType(Types.VARCHAR).withSize(30).notNull());
        addMetadata(userType, ColumnMetadata.named("user_type").withIndex(5).ofType(Types.VARCHAR).withSize(2));
    }

}

