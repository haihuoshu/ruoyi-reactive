package org.huanzhang.framework.r2dbc.repository;

import org.huanzhang.framework.r2dbc.entity.AbstractAuditable;
import org.huanzhang.framework.security.ReactiveSecurityUtils;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface AuditableRepository<T extends AbstractAuditable> {

    default Mono<T> beforeInsert(T entity) {
        return ReactiveSecurityUtils.getUsername()
                .map(username -> {
                    entity.setCreateBy(username);
                    entity.setCreateTime(LocalDateTime.now());
                    entity.setUpdateBy(username);
                    entity.setUpdateTime(LocalDateTime.now());
                    return entity;
                });
    }

    default Mono<T> beforeUpdate(T entity) {
        return ReactiveSecurityUtils.getUsername()
                .map(username -> {
                    entity.setUpdateBy(username);
                    entity.setUpdateTime(LocalDateTime.now());
                    return entity;
                });
    }

}
