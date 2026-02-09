package com.mainapp.openai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.Connection;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource primaryDataSource() {
        System.out.println("DataSource is created for H2 Sql.");
        return DataSourceBuilder.create().build();
    }
    @Bean("postgresDataSource")
    @ConfigurationProperties("spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        DataSource dataSource = DataSourceBuilder.create().build();
        System.out.println("DataSource is created for psotgres Sql.");
        return dataSource;
    }
}
