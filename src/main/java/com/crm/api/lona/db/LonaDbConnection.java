package com.crm.api.lona.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "lonaEntityManagerFactory",
        transactionManagerRef = "lonaTransactionManager",
        basePackages = { "com.crm.api.lona" }
)
public class LonaDbConnection {
    @Bean(name = "lonaDbDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.lona")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "lonaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("lonaDbDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.crm.api.lona")
                .persistenceUnit("lonaDb")
                .build();
    }

    @Bean(name = "lonaTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("lonaEntityManagerFactory") EntityManagerFactory
                    entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
