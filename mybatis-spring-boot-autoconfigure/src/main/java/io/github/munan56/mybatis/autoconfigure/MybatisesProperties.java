package io.github.munan56.mybatis.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author munan
 * @version 1.0
 */

@ConfigurationProperties(prefix = MybatisesProperties.MYBATIS_PREFIX)
public class MybatisesProperties {
    public static final String MYBATIS_PREFIX = "multiple";

    private Map<String, MybatisProperties> mybatis;

    public Map<String, MybatisProperties> getMybatis() {
        return mybatis;
    }

    public MybatisesProperties() {
    }

    public MybatisesProperties(Map<String, MybatisProperties> mybatis) {
        this.mybatis = mybatis;
    }

    public void setMybatis(Map<String, MybatisProperties> mybatis) {
        this.mybatis = mybatis;
    }
}
