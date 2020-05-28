//package com.aa.boot.mybatis.autoconfigure;
//
//import org.apache.ibatis.annotations.Mapper;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.mapper.ClassPathMapperScanner;
//import org.mybatis.spring.mapper.MapperFactoryBean;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.BeanFactoryAware;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.ResourceLoaderAware;
//import org.springframework.context.annotation.Conditional;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.core.type.AnnotationMetadata;
//import org.springframework.util.StringUtils;
//
//import java.util.List;
//
///**
// * @author munan
// * @version 1.0
// * @date 2020/5/26 16:50
// */
//
////@Import(MybatisAutoConfiguration.class)
//@Configuration
//@AutoConfigureAfter(MybatisAutoConfiguration.class)
//public class MapperConfiguration implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware{
//    private static final Logger logger = LoggerFactory.getLogger(MapperConfiguration.class);
//    private BeanFactory beanFactory;
//
//    private ResourceLoader resourceLoader;
//    @Override
//    public void pes.registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
//
//        if (!AutoConfigurationPackages.has(this.beanFactory)) {
//            logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
//            return;
//        }
//
//        logger.info("Searching for mappers annotated with @Mapper");
//
//        List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
//        if (logger.isDebugEnabled()) {
//            packages.forEach(pkg -> logger.debug("Using auto-configuration base package '{}'", pkg));
//        }
//
////            bean.getConfigs().forEach((k,v)->{
//        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
//        if (this.resourceLoader != null) {
//            scanner.setResourceLoader(this.resourceLoader);
//        }
//        scanner.setAnnotationClass(Mapper.class);
//        scanner.registerFilters();
//        scanner.setSqlSessionFactoryBeanName("aaa" + "SqlSessionFactory");
//        scanner.setSqlSessionTemplateBeanName("aaa" + "SqlSessionTemplate");
//        scanner.doScan(StringUtils.toStringArray(packages));
////            });
//
//    }
//
//    @Override
//    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
//        this.beanFactory = beanFactory;
//    }
//
//    @Override
//    public void setResourceLoader(ResourceLoader resourceLoader) {
//        this.resourceLoader =resourceLoader;
//    }
//
//
//    /**
//     * {@link org.mybatis.spring.annotation.MapperScan} ultimately ends up
//     * creating instances of {@link MapperFactoryBean}. If
//     * {@link org.mybatis.spring.annotation.MapperScan} is used then this
//     * auto-configuration is not needed. If it is _not_ used, however, then this
//     * will bring in a bean registrar and automatically register components based
//     * on the same component-scanning path as Spring Boot itself.
//     */
//    @org.springframework.context.annotation.Configuration
//    @Import(MapperConfiguration.class)
//    @ConditionalOnMissingBean(MapperFactoryBean.class)
//    public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean {
//
//        @Override
//        public void afterPropertiesSet() {
//            logger.info("Mine No {} found.", MapperFactoryBean.class.getName());
//        }
//    }
//
//}
