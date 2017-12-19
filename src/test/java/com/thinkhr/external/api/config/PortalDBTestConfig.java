package com.thinkhr.external.api.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.CollectionUtils;

import com.thinkhr.external.api.utils.ApiTestDataUtil;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "com.thinkhr.external.api.repositories" },
                       entityManagerFactoryRef = "entityManagerFactory", 
                       transactionManagerRef = "transactionManager"
)
public class PortalDBTestConfig {

    @Value("${hibernate.dialect}")
    private String dialect;

    @Value("${hibernate.show_sql}")
    private String showSql;

    @Value("${hibernate.hbm2ddl.auto}")
    private String ddlAuto;

    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    @Profile("testPortal")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
            @Qualifier("dataSource") DataSource dataSource) {

        return builder.dataSource(dataSource).packages("com.thinkhr.external.api.db.entities")
                .properties(additionalProperties()).build();
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }

    final Map<String, Object> additionalProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        final Properties hibernateProperties = new Properties();

        hibernateProperties.setProperty(ApiTestDataUtil.HIBERNATE_HBM2DDL_AUTO, ddlAuto);
        hibernateProperties.setProperty(ApiTestDataUtil.HIBERNATE_DIALECT, dialect);
        hibernateProperties.setProperty(ApiTestDataUtil.HIBERNATE_SHOW_SQL, showSql);

        CollectionUtils.mergePropertiesIntoMap(hibernateProperties, properties);

        return properties;
    }

}
