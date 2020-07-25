package io.github.munan56.mybatis.autoconfigure;


import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author munan
 * @version 1.0
 */
@org.springframework.context.annotation.Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@EnableConfigurationProperties({MybatisesProperties.class, MybatisProperties.class})
public class MybatisAutoConfiguration implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private static final Logger logger = LoggerFactory.getLogger(MybatisAutoConfiguration.class);


    private static final String DATASOURCE = "DataSource";

    private static final String SQLSESSIONFACTORY = "SqlSessionFactory";

    private static final String SQLSESSIONTEMPLETE = "SqlSessionTemplete";

    private static final String TRANSACTIONMANAGER = "TransactionManager";


    private MybatisesProperties multipleMybatisProperties;

    private MybatisProperties mybatisProperties;

    private Environment environment;
    //
    private Interceptor[] interceptors;

    private ResourceLoader resourceLoader;

    private DatabaseIdProvider databaseIdProvider;

    private List<ConfigurationCustomizer> configurationCustomizers;


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ConfigurationProperties multiple = MybatisesProperties.class.getAnnotation(ConfigurationProperties.class);
        ConfigurationProperties mybatis = MybatisProperties.class.getAnnotation(ConfigurationProperties.class);
        BindResult<MybatisesProperties> multipleBind = Binder.get(this.environment).bind(multiple.prefix(), MybatisesProperties.class);
        this.multipleMybatisProperties = multipleBind.isBound() ? multipleBind.get() : null;
        BindResult<MybatisProperties> mybatisBind = Binder.get(this.environment).bind(mybatis.prefix(), MybatisProperties.class);
        this.mybatisProperties = mybatisBind.isBound() ? mybatisBind.get() : null;
        if (Objects.nonNull(this.mybatisProperties) && Objects.isNull(this.multipleMybatisProperties)) {
            logger.info("Register Single DataSource with Mybatis ");
        }
        if (Objects.nonNull(this.multipleMybatisProperties) && Objects.isNull(this.mybatisProperties)) {
            logger.info("Register multiple DataSource with Mybatis ");
        }
        if (Objects.isNull(this.mybatisProperties) && Objects.isNull(this.multipleMybatisProperties)) {
            logger.error("Could not found Mybatis configuration with prefix {} or {} Check your replica set configuration!", MybatisProperties.MYBATIS_PREFIX, MybatisProperties.MYBATIS_PREFIX);
            return;
        }
        try {
            registerMissBeanDefinitions(registry);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void registerMissBeanDefinitions(BeanDefinitionRegistry registry) throws Exception {

        if (Objects.nonNull(this.multipleMybatisProperties)) {
            boolean primary = true;
            for (Map.Entry<String, MybatisProperties> entry : multipleMybatisProperties.getMybatis().entrySet()) {
                registryMybatis(entry.getKey(), primary, entry.getValue(), registry);
                primary = false;
            }
        }
        if (Objects.nonNull(this.mybatisProperties)) {
            registryMybatis(null, true, this.mybatisProperties, registry);
        }
    }


    private void registryMybatis(String prefix, boolean primary, MybatisProperties properties, BeanDefinitionRegistry registry) throws Exception {

        DataSource build = properties.initializeDataSourceBuilder().build();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSource.class, () -> build);
        builder.setPrimary(primary);
        registry.registerBeanDefinition(prefix + DATASOURCE, builder.getBeanDefinition());

        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(build);
        if (StringUtils.hasText(properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(properties.getConfigLocation()));
        }
        applyConfiguration(factory, properties);
        if (properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(properties.getConfigurationProperties());
        }
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }
        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(properties.getTypeAliasesPackage());
        }
        if (properties.getTypeAliasesSuperType() != null) {
            factory.setTypeAliasesSuperType(properties.getTypeAliasesSuperType());
        }
        if (StringUtils.hasLength(properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(properties.resolveMapperLocations())) {
            factory.setMapperLocations(properties.resolveMapperLocations());
        }

        SqlSessionFactory sqlSessionFactory = factory.getObject();
        BeanDefinitionBuilder sqlSessionFactoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionFactory.class, () -> sqlSessionFactory);
        registry.registerBeanDefinition(prefix + SQLSESSIONFACTORY, sqlSessionFactoryBuilder.getBeanDefinition());


        SqlSessionTemplate sqlSessionTemplate;
        ExecutorType executorType = properties.getExecutorType();
        if (executorType != null) {
            sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory, executorType);
        } else {
            sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        }
        BeanDefinitionBuilder sqlSessionTemplateBuilder = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionTemplate.class, () -> sqlSessionTemplate);

        registry.registerBeanDefinition(prefix + SQLSESSIONTEMPLETE, sqlSessionTemplateBuilder.getBeanDefinition());

        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(build);
        BeanDefinitionBuilder transactionManagerBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class, () -> transactionManager);
        registry.registerBeanDefinition(prefix + TRANSACTIONMANAGER, transactionManagerBuilder.getBeanDefinition());
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

        if (this.resourceLoader != null) {
            scanner.setResourceLoader(this.resourceLoader);
        }
        scanner.registerFilters();
        scanner.setSqlSessionFactoryBeanName(prefix + SQLSESSIONFACTORY);
        scanner.doScan(properties.getMapperScanPackage());
    }


    private void applyConfiguration(SqlSessionFactoryBean factory, MybatisProperties properties) {
        Configuration configuration = properties.getConfiguration();
        if (configuration == null && !StringUtils.hasText(properties.getConfigLocation())) {
            configuration = new Configuration();
        }
        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        factory.setConfiguration(configuration);
    }

}
