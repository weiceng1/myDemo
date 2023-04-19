package com.demo.configuration;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;


@Configuration
@EnableConfigurationProperties(value = {DataSourceProperties.class})
public class DataSourceConfiguration {

    @Bean
    @Primary
//    @ConditionalOnProperty(value = "key-vault.enabled", havingValue = "false")
    public DataSource dataSource(DataSourceProperties properties ) throws SQLException {
        properties.setDataSource(new ConnectionDataSource(properties));
        return setUpDatasource(properties);
    }

//    @Bean
//    @Primary
//    @ConditionalOnProperty(value = "key-vault.enabled", havingValue = "true")
//    public DataSource dataSource(KeyVaultClient keyVaultClient, DataSourceProperties properties) throws SQLException {
//        String mysqlUrl = String.format(ConnectionDataSource.MYSQL_URL_FORMAT,
//                keyVaultClient.getSecret(configuration.getCustomerHostKey()), properties.getName());
//        logger.info("customer mysql connection url : {}", mysqlUrl);
//
//        properties.setUrl(mysqlUrl);
//        properties.setUsername(keyVaultClient.getSecret(configuration.getCustomerUserKey()));
//        properties.setPassword(keyVaultClient.getSecret(configuration.getCustomerPasswordKey()));
//
//        PoolProperties dbProperties = new PoolProperties();
//        BeanUtils.copyProperties(properties, dbProperties);
//        properties.setDataSource(new ConnectionDataSource(dbProperties));
//
//        return setUpDatasource(properties);
//    }

    private DataSource setUpDatasource(PoolProperties properties) throws SQLException {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setPoolProperties(properties);
        dataSource.createPool();
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }
}
