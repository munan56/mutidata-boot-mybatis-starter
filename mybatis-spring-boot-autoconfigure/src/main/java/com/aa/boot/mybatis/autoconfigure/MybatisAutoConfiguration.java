package com.aa.boot.mybatis.autoconfigure;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;
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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.*;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * @author munan
 * @version 1.0
 * @date 2020/6/8 17:10
 */
@org.springframework.context.annotation.Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@EnableConfigurationProperties({MybatisesProperties.class})
public class MybatisAutoConfiguration implements ImportBeanDefinitionRegistrar,EnvironmentAware, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MybatisAutoConfiguration.class);


    private  MybatisesProperties pes;

    private  Interceptor[] interceptors;

    private  ResourceLoader resourceLoader;

    private  DatabaseIdProvider databaseIdProvider;

    private  List<ConfigurationCustomizer> configurationCustomizers;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("start mine");

    }

    @Override
    public void setEnvironment(Environment environment) {
        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
        try {
            MybatisesProperties properties = properties(env);
            pes = properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MybatisesProperties properties(ConfigurableEnvironment env) throws IOException {
        Properties props = new Properties();
        MutablePropertySources propSrcs = env.getPropertySources();
        StreamSupport.stream(propSrcs.spliterator(), false)
                .filter(ps -> ps instanceof EnumerablePropertySource)
                .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
                .flatMap(Arrays::<String>stream)
                .filter(name->StringUtils.startsWithIgnoreCase(name,MybatisesProperties.MYBATIS_PREFIX))
                .forEach(propName -> props.setProperty(propName, env.getProperty(propName)));
        JavaPropsMapper propsMapper = new JavaPropsMapper();

        return propsMapper.readPropertiesAs(props,JavaPropsSchema.emptySchema().withPrefix(MybatisesProperties.MYBATIS_PREFIX),MybatisesProperties.class);

    }
//    public static Map<String,MybatisProperties> getPropertiesStartingWith( ConfigurableEnvironment aEnv, String aKeyPrefix ) {
//        Map<String,MybatisProperties> result = new HashMap<>();
//        Map<String,Object> map = getAllProperties( aEnv );
//        for (Map.Entry<String, Object> entry : map.entrySet()) {
//            String key = entry.getKey();
//            if ( key.startsWith( aKeyPrefix ) ) {
//                result.put( key, JSONObject.parseObject(JSONObject.toJSONString(entry.getValue()),MybatisProperties.class));
//            }
//        }
//        return result;
//
//    }


    public static Map<String,Object> getAllProperties( ConfigurableEnvironment aEnv ) {
        Map<String,Object> result = new HashMap<>();
        aEnv.getPropertySources().forEach( ps -> addAll( result, getAllProperties( ps ) ) );
        return result;

    }



    public static Map<String,Object> getAllProperties( PropertySource<?> aPropSource ) {

        Map<String,Object> result = new HashMap<>();
        if ( aPropSource instanceof CompositePropertySource) {
            CompositePropertySource cps = (CompositePropertySource) aPropSource;
            cps.getPropertySources().forEach( ps -> addAll( result, getAllProperties( ps ) ) );
            return result;
        }


        if ( aPropSource instanceof EnumerablePropertySource<?> ) {
            EnumerablePropertySource<?> ps = (EnumerablePropertySource<?>) aPropSource;
            Arrays.asList( ps.getPropertyNames() ).forEach(key -> result.put( key, ps.getProperty( key ) ) );
            return result;
        }
        return result;

    }


    private static void addAll( Map<String, Object> aBase, Map<String, Object> aToBeAdded ) {
        for (Map.Entry<String, Object> entry : aToBeAdded.entrySet()) {
            if ( aBase.containsKey( entry.getKey() ) ) {
                continue;
            }
            aBase.put( entry.getKey(), entry.getValue() );

        }

    }
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        try {
            datasource(registry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void datasource(BeanDefinitionRegistry registry) throws Exception {
        Map<String, MybatisProperties> mybatis = this.pes.getConfigs();
        Iterator<Map.Entry<String, MybatisProperties>> iterator = mybatis.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, MybatisProperties> next = iterator.next();
            String k = next.getKey();
            MybatisProperties v = next.getValue();

            DataSource build = v.initializeDataSourceBuilder().build();
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSource.class, () -> build);
            registry.registerBeanDefinition(k+"DataSource",builder.getBeanDefinition());


            SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
            factory.setDataSource(build);
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

            SqlSessionFactory   sqlSessionFactory = factory.getObject();
                BeanDefinitionBuilder sqlSessionFactoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionFactory.class, () -> sqlSessionFactory);
                registry.registerBeanDefinition(k+"SqlSessionFactory",sqlSessionFactoryBuilder.getBeanDefinition());



            SqlSessionTemplate sqlSessionTemplate;
            ExecutorType executorType = v.getExecutorType();
            if (executorType != null) {
                sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory, executorType);
            } else {
                sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
            }
            BeanDefinitionBuilder sqlSessionTemplateBuilder = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionTemplate.class, () -> sqlSessionTemplate);

            registry.registerBeanDefinition(k+"SqlSessionTemplate", sqlSessionTemplateBuilder.getBeanDefinition());

            DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(build);
            BeanDefinitionBuilder transactionManagerBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class, () -> transactionManager);
            registry.registerBeanDefinition(k + "TransactionManager", transactionManagerBuilder.getBeanDefinition());
            ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

            if (this.resourceLoader != null) {
                scanner.setResourceLoader(this.resourceLoader);
            }
            scanner.registerFilters();
            scanner.setSqlSessionFactoryBeanName(k + "SqlSessionFactory");
            scanner.doScan(v.getMapperScanPackage());
        }


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
