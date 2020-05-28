//package com.munan.boot.mybatis.autoconfigure;
//
//import com.zaxxer.hikari.HikariDataSource;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.boot.jdbc.DatabaseDriver;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.StringUtils;
//
//import javax.sql.DataSource;
//
///**
// * @author munan
// * @version 1.0
// * @date 2020/5/24 11:04
// */
//public class DataSourceConfiguration {
//
//
//    @SuppressWarnings("unchecked")
//    protected static <T> T createDataSource(MybatisProperties properties,
//                                            Class<? extends DataSource> type) {
//        return (T) properties.initializeDataSourceBuilder().type(type).build();
//    }
//
//    /**
//     * Tomcat Pool DataSource configuration.
//     */
//    @Configuration
//    @ConditionalOnClass(org.apache.tomcat.jdbc.pool.DataSource.class)
//    @ConditionalOnMissingBean(DataSource.class)
//    @ConditionalOnProperty(name = "mybatis.datasource.type",
//            havingValue = "org.apache.tomcat.jdbc.pool.DataSource", matchIfMissing = true)
//    static class Tomcat {
//
//        @Bean
//        @ConfigurationProperties(prefix = "mybatis.datasource.tomcat")
//        public org.apache.tomcat.jdbc.pool.DataSource dataSource(
//                MybatisProperties properties) {
//            org.apache.tomcat.jdbc.pool.DataSource dataSource = createDataSource(
//                    properties, org.apache.tomcat.jdbc.pool.DataSource.class);
//            DatabaseDriver databaseDriver = DatabaseDriver
//                    .fromJdbcUrl(properties.determineUrl());
//            String validationQuery = databaseDriver.getValidationQuery();
//            if (validationQuery != null) {
//                dataSource.setTestOnBorrow(true);
//                dataSource.setValidationQuery(validationQuery);
//            }
//            return dataSource;
//        }
//
//    }
//
//    /**
//     * Hikari DataSource configuration.
//     */
//    @Configuration
//    @ConditionalOnClass(HikariDataSource.class)
//    @ConditionalOnMissingBean(DataSource.class)
//    @ConditionalOnProperty(name = "mybatis.datasource.type",
//            havingValue = "com.zaxxer.hikari.HikariDataSource", matchIfMissing = true)
//    static class Hikari {
//
//        @Bean
//        @ConfigurationProperties(prefix = "mybatis.datasource.hikari")
//        public HikariDataSource dataSource(MybatisProperties properties) {
//            HikariDataSource dataSource = createDataSource(properties,
//                    HikariDataSource.class);
//            if (StringUtils.hasText(properties.getName())) {
//                dataSource.setPoolName(properties.getName());
//            }
//            return dataSource;
//        }
//
//    }
//
//    /**
//     * DBCP DataSource configuration.
//     */
//    @Configuration
//    @ConditionalOnClass(org.apache.commons.dbcp2.BasicDataSource.class)
//    @ConditionalOnMissingBean(DataSource.class)
//    @ConditionalOnProperty(name = "mybatis.datasource.type",
//            havingValue = "org.apache.commons.dbcp2.BasicDataSource",
//            matchIfMissing = true)
//    static class Dbcp2 {
//
//        @Bean
//        @ConfigurationProperties(prefix = "mybatis.datasource.dbcp2")
//        public org.apache.commons.dbcp2.BasicDataSource dataSource(
//                MybatisProperties properties) {
//            return createDataSource(properties,
//                    org.apache.commons.dbcp2.BasicDataSource.class);
//        }
//
//    }
//
//    /**
//     * Generic DataSource configuration.
//     */
//    @Configuration
//    @ConditionalOnMissingBean(DataSource.class)
//    @ConditionalOnProperty(name = "mybatis.datasource.type")
//    static class Generic {
//
//        @Bean
//        public DataSource dataSource(MybatisProperties properties) {
//            return properties.initializeDataSourceBuilder().build();
//        }
//
//    }
//
//
//}
