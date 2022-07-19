package io.spring.infrastructure.jpa.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
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

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = {"io.spring.entity"})
@EnableJpaRepositories(
        entityManagerFactoryRef = "mysqlEntityManagerFactory",
        basePackages = {"io.spring.infrastructure.jpa.repository"})
public class MysqlConfig {

    @Bean(name = "second-datasource")
    @ConfigurationProperties(prefix = "spring.second-datasource.hikari")
    public DataSource secondDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("second-datasource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("io.spring.entity")
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            @Qualifier("mysqlEntityManagerFactory") LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(mysqlEntityManagerFactory.getObject()));
    }
}
