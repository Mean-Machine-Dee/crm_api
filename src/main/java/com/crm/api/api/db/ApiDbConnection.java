package com.crm.api.api.db;

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
        entityManagerFactoryRef = "apiDbEntityManagerFactory",
        transactionManagerRef = "apiDbTransactionManager",
        basePackages = { "com.crm.api.api" }
)
public class ApiDbConnection {

    @Bean(name = "apiDbDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.api")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean(name = "apiDbEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("apiDbDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.crm.api.api")
                .persistenceUnit("apiDb")
                .build();
    }


    @Bean(name = "apiDbTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("apiDbEntityManagerFactory") EntityManagerFactory
                    entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
