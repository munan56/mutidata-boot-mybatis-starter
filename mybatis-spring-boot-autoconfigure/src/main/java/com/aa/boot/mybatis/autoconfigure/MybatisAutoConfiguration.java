package com.aa.boot.mybatis.autoconfigure;

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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author munan
 * @version 1.0
 * @date 2020/5/23 18:22
 */

@Order(Ordered.HIGHEST_PRECEDENCE)
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
        mapper();

    }

    private void mapper() {

        pes.getConfigs().forEach((k,v)->{
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(AutoConfiguredMapperScannerRegistrar.sThreadLocal.get());
        if (this.resourceLoader != null) {
            scanner.setResourceLoader(this.resourceLoader);
        }
        scanner.setAnnotationClass(Mapper.class);
        scanner.registerFilters();
        scanner.setSqlSessionFactoryBeanName(k + "SqlSessionFactory");
        scanner.setSqlSessionTemplateBeanName(k + "SqlSessionTemplate");
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
            registerBean(k + "DataSource", build);
        });
    }

    public void registerBean(String name, Object instance) {
        configurableListableBeanFactory.registerSingleton(name, instance);
    }

    public <T> T getBean(String beanName, Class<T> clazz) {

        return configurableListableBeanFactory.getBean(beanName, clazz);
    }


    public void sqlSessionFactory() throws Exception {
        Map<String, MybatisProperties> mybatis = pes.getConfigs();
        Set<Map.Entry<String, MybatisProperties>> entries = mybatis.entrySet();
        Iterator<Map.Entry<String, MybatisProperties>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, MybatisProperties> next = iterator.next();
            String k = next.getKey();
            MybatisProperties v = next.getValue();
            SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
            factory.setDataSource(this.getBean(k + "DataSource", DataSource.class));
//            factory.setVfs(SpringBootVFS.class);
            if (StringUtils.hasText(v.getConfigLocation())) {
                factory.setConfigLocation(this.resourceLoader.getResource(v.getConfigLocation()));
            }
            applyConfiguration(factory, v);
            if (v.getConfigurationProperties() != null) {
                factory.setConfigurationProperties(v.getConfigurationProperties());
            }
            if (!ObjectUtils.isEmpty(this.interceptors)) {
                factory.setPlugins(this.interceptors);
            }
            if (this.databaseIdProvider != null) {
                factory.setDatabaseIdProvider(this.databaseIdProvider);
            }
            if (StringUtils.hasLength(v.getTypeAliasesPackage())) {
                factory.setTypeAliasesPackage(v.getTypeAliasesPackage());
            }
            if (v.getTypeAliasesSuperType() != null) {
                factory.setTypeAliasesSuperType(v.getTypeAliasesSuperType());
            }
            if (StringUtils.hasLength(v.getTypeHandlersPackage())) {
                factory.setTypeHandlersPackage(v.getTypeHandlersPackage());
            }
            if (!ObjectUtils.isEmpty(v.resolveMapperLocations())) {
                factory.setMapperLocations(v.resolveMapperLocations());
            }

            SqlSessionFactory sqlSessionFactory = factory.getObject();

            registerBean(k + "SqlSessionFactory", sqlSessionFactory);
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
            SqlSessionFactory sqlSessionFactory = this.getBean(k + "SqlSessionFactory", SqlSessionFactory.class);
            ExecutorType executorType = v.getExecutorType();
            if (executorType != null) {
                sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory, executorType);
            } else {
                sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
            }
            registerBean(k + "SqlSessionTemplate", sqlSessionTemplate);
        });

    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        logger.info("Searching for mappers annotated with @Mapper register " + beanName);
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
    @AutoConfigureAfter(MybatisesProperties.class)
    public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean {

        @Override
        public void afterPropertiesSet() {
            logger.info("Mine No {} found.", MapperFactoryBean.class.getName());
        }
    }


}
