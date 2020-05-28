package com.aa.boot.mybatis.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author munan
 * @version 1.0
 * @date 2020/5/23 18:23
 */

@ConfigurationProperties(prefix = MybatisesProperties.MYBATIS_PREFIX)
public class MybatisesProperties implements BeanClassLoaderAware, InitializingBean{

    public static final String MYBATIS_PREFIX = "mine";
    private ClassLoader classLoader;

    private Map<String, MybatisProperties> configs;


    public Map<String, MybatisProperties> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, MybatisProperties> configs) {
        this.configs = configs;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(this.getConfigs());
    }



}
