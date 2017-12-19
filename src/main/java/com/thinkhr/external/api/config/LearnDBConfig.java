package com.thinkhr.external.api.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database configuration file to manage thinkhr_learn datasource
 * 
 * @author Ajay Jain
 * @since 2017-12-15
 *
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryLearn", 
        transactionManagerRef = "transactionManagerLearn", 
        basePackages = { "com.thinkhr.external.api.learn.repositories" }
)
public class LearnDBConfig {
    
    @Bean(name = "dataSourceLearn")
    @ConfigurationProperties(prefix = "learn.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "entityManagerFactoryLearn")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dataSourceLearn") DataSource dataSource) {

        return builder.dataSource(dataSource).packages("com.thinkhr.external.api.db.learn.entities").build();
    }

    @Bean(name = "transactionManagerLearn")
    public PlatformTransactionManager transactionManager( 
            @Qualifier("entityManagerFactoryLearn") EntityManagerFactory entityManagerFactory) {
        
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "learnJdbcTemplate")
    public JdbcTemplate learnJdbcTemplate(@Qualifier("dataSourceLearn") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}