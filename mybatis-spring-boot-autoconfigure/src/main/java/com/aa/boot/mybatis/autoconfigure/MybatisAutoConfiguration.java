package com.aa.boot.mybatis.autoconfigure;

import com.google.common.collect.ImmutableMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.*;

/**
 * @author munan
 * @version 1.0
 * @date 2020/5/23 18:22
 */


@org.springframework.context.annotation.Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@EnableConfigurationProperties({MybatisesProperties.class})
public class MybatisAutoConfiguration implements InitializingBean, BeanPostProcessor,ApplicationContextAware  {

    private static final Logger logger = LoggerFactory.getLogger(MybatisAutoConfiguration.class);

    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    private final MybatisesProperties pes;

    private final Interceptor[] interceptors;

    private final ResourceLoader resourceLoader;

    private final DatabaseIdProvider databaseIdProvider;

    private final List<ConfigurationCustomizer> configurationCustomizers;


    public MybatisAutoConfiguration(
            MybatisesProperties pes,
            ObjectProvider<Interceptor[]> interceptorsProvider,
            ResourceLoader resourceLoader,
            ObjectProvider<DatabaseIdProvider> databaseIdProvider,
            ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) throws Exception {
//        this.p = p;
        this.pes = pes;
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();

    }


    @Override
    public void afterPropertiesSet() throws Exception {
        checkConfigFileExists();
        datasource();
        sqlSessionFactory();
        sqlSessionTemplate();
        manager();
        mapper();

    }

    private void manager() {
        pes.getConfigs().forEach((k,v)->{
//            DataSource dataSource = this.getBean(k + "DataSource", DataSource.class);
            PlatformTransactionManager transactionManager = new DataSourceTransactionManager();
            registerBean(k + "TransactionManager", transactionManager.getClass(),null,ImmutableMap.of("dataSource",k + "DataSource"),null);
        });

    }


    private void mapper() {

        pes.getConfigs().forEach((k,v)->{
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(AutoConfiguredMapperScannerRegistrar.sThreadLocal.get());
        if (this.resourceLoader != null) {
            scanner.setResourceLoader(this.resourceLoader);
        }
        scanner.registerFilters();
        scanner.setSqlSessionFactoryBeanName(k + "SqlSessionFactory");
        scanner.doScan(v.getMapperScanPackage());
            });
    }

    private void checkConfigFileExists() {
        logger.info("start mine");
    }



    public void datasource() {
        Map<String, MybatisProperties> mybatis = this.pes.getConfigs();
        mybatis.forEach((k, v) -> {
            DataSource build = v.initializeDataSourceBuilder().build();

//            registerBean(k + "DataSource", build);
            registerBean(k+"DataSource", build.getClass(), ImmutableMap.of("jdbcUrl",v.getUrl(),"username",v.getUsername(),"password",v.getPassword()),null,null);
//            PlatformTransactionManager transactionManager = new DataSourceTransactionManager(build);
//            registerBean(k + "TransactionManager", transactionManager);
//            transactionManager.
//            BeanDefinitionRegistry beanDefinitionRegistry = AutoConfiguredMapperScannerRegistrar.sThreadLocal.get();
//            RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
//            rootBeanDefinition.setBeanClass(DataSourceTransactionManager.class);
//            MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
//            mutablePropertyValues.add("dataSource",build);
//            rootBeanDefinition.setPropertyValues(mutablePropertyValues);
//            beanDefinitionRegistry.registerBeanDefinition("aaa",rootBeanDefinition);
        });
    }

//    public void registerBean(String name, Object instance) {
//        configurableListableBeanFactory.registerSingleton(name, instance);
//    }
    public void registerBean(String name, Class<?> beanClass,Map<String,Object> props,Map<String,String> refs,Map<String,String> cons) {
        BeanDefinitionRegistry registry = AutoConfiguredMapperScannerRegistrar.sThreadLocal.get();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        if (!CollectionUtils.isEmpty(props)){
            props.forEach(builder::addPropertyValue);
        }
        if (!CollectionUtils.isEmpty(refs)){
            refs.forEach(builder::addPropertyReference);
        }
        if (!CollectionUtils.isEmpty(cons)){
            cons.forEach((beanName, beanName2) -> builder.addConstructorArgReference(beanName2));
        }
        registry.registerBeanDefinition(name,builder.getBeanDefinition());
    }

//    public <T> T getBean(String beanName, Class<T> clazz) {
//
//        return configurableListableBeanFactory.getBean(beanName, clazz);
//    }


    public void sqlSessionFactory() throws Exception {
        Map<String, MybatisProperties> mybatis = pes.getConfigs();
        Set<Map.Entry<String, MybatisProperties>> entries = mybatis.entrySet();
        Iterator<Map.Entry<String, MybatisProperties>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> props = new HashMap<>();
            Map.Entry<String, MybatisProperties> next = iterator.next();
            String k = next.getKey();
            MybatisProperties v = next.getValue();
            SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
//            factory.setDataSource(this.getBean(k + "DataSource", DataSource.class));
//            factory.setVfs(SpringBootVFS.class);
            if (StringUtils.hasText(v.getConfigLocation())) {
                factory.setConfigLocation(this.resourceLoader.getResource(v.getConfigLocation()));
                props.put("configLocation",this.resourceLoader.getResource(v.getConfigLocation()));
            }
            applyConfiguration(factory, v);
            if (v.getConfigurationProperties() != null) {
                factory.setConfigurationProperties(v.getConfigurationProperties());
                props.put("configurationProperties",v.getConfigurationProperties());
            }
            if (!ObjectUtils.isEmpty(this.interceptors)) {
                factory.setPlugins(this.interceptors);
                props.put("plugins", this.interceptors);
            }
            if (this.databaseIdProvider != null) {
                factory.setDatabaseIdProvider(this.databaseIdProvider);
                props.put("databaseIdProvider", this.databaseIdProvider);
            }
            if (StringUtils.hasLength(v.getTypeAliasesPackage())) {
                factory.setTypeAliasesPackage(v.getTypeAliasesPackage());
                props.put("typeAliasesPackage", v.getTypeAliasesPackage());
            }
            if (v.getTypeAliasesSuperType() != null) {
                factory.setTypeAliasesSuperType(v.getTypeAliasesSuperType());
                props.put("typeAliasesSuperType", v.getTypeAliasesSuperType());
            }
            if (StringUtils.hasLength(v.getTypeHandlersPackage())) {
                factory.setTypeHandlersPackage(v.getTypeHandlersPackage());
                props.put("typeHandlersPackage", v.getTypeHandlersPackage());
            }
            if (!ObjectUtils.isEmpty(v.resolveMapperLocations())) {
                factory.setMapperLocations(v.resolveMapperLocations());
                props.put("mapperLocations", v.resolveMapperLocations());
            }

//            SqlSessionFactory sqlSessionFactory = factory.getObject();
//
//            registerBean(k + "SqlSessionFactory", sqlSessionFactory);
            registerBean(k + "SqlSessionFactory", SqlSessionFactoryBean.class, props,ImmutableMap.of("dataSource",k +"DataSource"),null);
        }

    }

    //
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


    public void sqlSessionTemplate() {
        pes.getConfigs().forEach((k, v) -> {
            SqlSessionTemplate sqlSessionTemplate;
//            SqlSessionFactory sqlSessionFactory = this.getBean(k + "SqlSessionFactory", SqlSessionFactory.class);
            ExecutorType executorType = v.getExecutorType();
//            Map<String,String> map = new HashMap<>();
//            if (executorType != null) {
//                sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory, executorType);
////                map.put("1",sqlSessionFactory)
//            } else {
//                sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
//            }
//            registerBean(k + "SqlSessionTemplate", sqlSessionTemplate);
            registerBean(k + "SqlSessionTemplate", SqlSessionTemplate.class,null,null,ImmutableMap.of("1",k + "SqlSessionFactory"));
        });

    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MapperFactoryBean){
            logger.info("Searching for mappers annotated with @Mapper register " + beanName);
        }
        return bean;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        this.configurableListableBeanFactory = context.getBeanFactory();
    }




    /**
     * This will just scan the same base package as Spring Boot does. If you want
     * more power, you can explicitly use
     * {@link org.mybatis.spring.annotation.MapperScan} but this will get typed
     * mappers working correctly, out-of-the-box, similar to using Spring Data JPA
     * repositories.
     */


    public static class AutoConfiguredMapperScannerRegistrar
            implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {
        static final ThreadLocal<BeanDefinitionRegistry> sThreadLocal = new ThreadLocal();
        private BeanFactory beanFactory;

        private ResourceLoader resourceLoader;

        @Value("${mine.configs}")
        private Map<String,MybatisesProperties> pes;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

            if (!AutoConfigurationPackages.has(this.beanFactory)) {
                logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
                return;
            }

            logger.info("Searching for mappers annotated with @Mapper");
            sThreadLocal.set(registry);

        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }
    }

    /**
     * {@link org.mybatis.spring.annotation.MapperScan} ultimately ends up
     * creating instances of {@link MapperFactoryBean}. If
     * {@link org.mybatis.spring.annotation.MapperScan} is used then this
     * auto-configuration is not needed. If it is _not_ used, however, then this
     * will bring in a bean registrar and automatically register components based
     * on the same component-scanning path as Spring Boot itself.
     */
    @org.springframework.context.annotation.Configuration
    @Import({MybatisAutoConfiguration.AutoConfiguredMapperScannerRegistrar.class})
    @ConditionalOnMissingBean(MapperFactoryBean.class)
    public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean {

        @Override
        public void afterPropertiesSet() {
            logger.info("Mine No {} found.", MapperFactoryBean.class.getName());
        }
    }


}
