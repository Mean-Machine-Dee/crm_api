package com.crm.api.sdk.db;
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
        entityManagerFactoryRef = "sdkDbEntityManagerFactory",
        transactionManagerRef = "sdkDbTransactionManager",
        basePackages = { "com.crm.api.sdk" }
)
public class SdkDbConnection {

    @Bean(name = "sdkDbDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.sdk")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean(name = "sdkDbEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("sdkDbDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.crm.api.sdk")
                .persistenceUnit("sdkDb")
                .build();
    }


    @Bean(name = "sdkDbTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("sdkDbEntityManagerFactory") EntityManagerFactory
                    entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}