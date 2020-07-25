package io.github.munan56.mybatis.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author munan
 * @version 1.0
 */

@ConfigurationProperties(prefix = MybatisesProperties.MYBATIS_PREFIX)
public class MybatisesProperties {
    private static final Logger logger = LoggerFactory.getLogger(MybatisesProperties.class);
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
