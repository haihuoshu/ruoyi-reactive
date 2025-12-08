package org.huanzhang.framework.r2dbc;

import com.querydsl.r2dbc.R2DBCConnectionProvider;
import com.querydsl.r2dbc.R2DBCQueryFactory;
import com.querydsl.r2dbc.SQLTemplates;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import reactor.core.publisher.Mono;

@Configuration
public class R2dbcConfiguration {

    @Bean
    public R2DBCConnectionProvider provider(ConnectionFactory connectionFactory) {
        return () -> Mono.from(connectionFactory.create());
    }

    @Bean
    public R2DBCQueryFactory queryFactory(R2DBCConnectionProvider provider) {
        return new R2DBCQueryFactory(SQLTemplates.DEFAULT, provider);
    }

    @Bean
    public R2dbcTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

}
