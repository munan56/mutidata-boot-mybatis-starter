/**
 *    Copyright 2015-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.aa.boot.mybatis.autoconfigure;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Configuration properties for MyBatis.
 *
 * @author Eddú Meléndez
 * @author Kazuki Shimizu
 */
public class MybatisProperties2222222222 implements BeanClassLoaderAware , InitializingBean {

  private ClassLoader classLoader;
  private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

  private String url;

  private String username;

  private String password;

  /**
   * Fully qualified name of the connection pool implementation to use. By default, it
   * is auto-detected from the classpath.
   */
  private Class<? extends DataSource> type;

  /**
   * Fully qualified name of the JDBC driver. Auto-detected based on the URL by default.
   */
  private String driverClassName;

  /**
   * Name of the datasource. Default to "testdb" when using an embedded database.
   */
  private String name;

  /**
   * Whether to generate a random datasource name.
   */
  private boolean generateUniqueName;

  private String uniqueName;


  private EmbeddedDatabaseConnection embeddedDatabaseConnection = EmbeddedDatabaseConnection.NONE;

  /**
   * Location of MyBatis xml config file.
   */
  private String configLocation;

  /**
   * Locations of MyBatis mapper files.
   */
  private String[] mapperLocations;

  /**
   * Packages to search type aliases. (Package delimiters are ",; \t\n")
   */
  private String typeAliasesPackage;

  /**
   * The super class for filtering type alias.
   * If this not specifies, the MyBatis deal as type alias all classes that searched from typeAliasesPackage.
   */
  private Class<?> typeAliasesSuperType;

  /**
   * Packages to search for type handlers. (Package delimiters are ",; \t\n")
   */
  private String typeHandlersPackage;

  /**
   * Indicates whether perform presence check of the MyBatis xml config file.
   */
  private boolean checkConfigLocation = false;

  /**
   * Execution mode for {@link org.mybatis.spring.SqlSessionTemplate}.
   */
  private ExecutorType executorType;

  /**
   * Externalized properties for MyBatis configuration.
   */
  private Properties configurationProperties;

  /**
   * A Configuration object for customize default settings. If {@link #configLocation}
   * is specified, this property is not used.
   */
  @NestedConfigurationProperty
  private Configuration configuration;

  /**
   * @since 1.1.0
   */
  public String getConfigLocation() {
    return this.configLocation;
  }

  /**
   * @since 1.1.0
   */
  public void setConfigLocation(String configLocation) {
    this.configLocation = configLocation;
  }

  public String[] getMapperLocations() {
    return this.mapperLocations;
  }

  public void setMapperLocations(String[] mapperLocations) {
    this.mapperLocations = mapperLocations;
  }

  public String getTypeHandlersPackage() {
    return this.typeHandlersPackage;
  }

  public void setTypeHandlersPackage(String typeHandlersPackage) {
    this.typeHandlersPackage = typeHandlersPackage;
  }

  public String getTypeAliasesPackage() {
    return this.typeAliasesPackage;
  }

  public void setTypeAliasesPackage(String typeAliasesPackage) {
    this.typeAliasesPackage = typeAliasesPackage;
  }

  /**
   * @since 1.3.3
   */
  public Class<?> getTypeAliasesSuperType() {
    return typeAliasesSuperType;
  }

  /**
   * @since 1.3.3
   */
  public void setTypeAliasesSuperType(Class<?> typeAliasesSuperType) {
    this.typeAliasesSuperType = typeAliasesSuperType;
  }

  public boolean isCheckConfigLocation() {
    return this.checkConfigLocation;
  }

  public void setCheckConfigLocation(boolean checkConfigLocation) {
    this.checkConfigLocation = checkConfigLocation;
  }

  public ExecutorType getExecutorType() {
    return this.executorType;
  }

  public void setExecutorType(ExecutorType executorType) {
    this.executorType = executorType;
  }

  /**
   * @since 1.2.0
   */
  public Properties getConfigurationProperties() {
    return configurationProperties;
  }

  /**
   * @since 1.2.0
   */
  public void setConfigurationProperties(Properties configurationProperties) {
    this.configurationProperties = configurationProperties;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }
  public void setType(Class<? extends DataSource> type) {
    this.type = type;
  }

  public Class<? extends DataSource> getType() {
    return this.type;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDriverClassName() {
    return driverClassName;
  }

  public void setDriverClassName(String driverClassName) {
    this.driverClassName = driverClassName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isGenerateUniqueName() {
    return generateUniqueName;
  }

  public void setGenerateUniqueName(boolean generateUniqueName) {
    this.generateUniqueName = generateUniqueName;
  }

  public String getUniqueName() {
    return uniqueName;
  }

  public void setUniqueName(String uniqueName) {
    this.uniqueName = uniqueName;
  }


  public ClassLoader getClassLoader() {
    return this.classLoader;
  }

//  public Resource[] resolveMapperLocations() {
//    return Stream.of(Optional.ofNullable(this.mapperLocations).orElse(new String[0]))
//        .flatMap(location -> Stream.of(getResources(location)))
//        .toArray(Resource[]::new);
//  }
//
//  private Resource[] getResources(String location) {
//    try {
//      return resourceResolver.getResources(location);
//    } catch (IOException e) {
//      return new Resource[0];
//    }
//  }

//  /**
//   * Determine the url to use based on this configuration and the environment.
//   * @return the url to use
//   * @since 1.4.0
//   */
//  public String determineUrl() {
//    if (StringUtils.hasText(this.url)) {
//      return this.url;
//    }
//    String databaseName = determineDatabaseName();
//    String url = (databaseName != null)
//            ? this.embeddedDatabaseConnection.getUrl(databaseName) : null;
//    if (!StringUtils.hasText(url)) {
//      throw new MybatisProperties2222222222.DataSourceBeanCreationException(
//              "Failed to determine suitable jdbc url", this,
//              this.embeddedDatabaseConnection);
//    }
//    return url;
//  }
  /**
   * Determine the name to used based on this configuration.
   * @return the database name to use or {@code null}
   * @since 2.0.0
   */
  public String determineDatabaseName() {
    if (this.generateUniqueName) {
      if (this.uniqueName == null) {
        this.uniqueName = UUID.randomUUID().toString();
      }
      return this.uniqueName;
    }
    if (StringUtils.hasLength(this.name)) {
      return this.name;
    }
    if (this.embeddedDatabaseConnection != EmbeddedDatabaseConnection.NONE) {
      return "testdb";
    }
    return null;
  }

  @Override
  public void setBeanClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    this.embeddedDatabaseConnection = EmbeddedDatabaseConnection
            .get(this.classLoader);
  }
//
//  static class DataSourceBeanCreationException extends BeanCreationException {
//
//    private final MybatisProperties2222222222 properties;
//
//    private final EmbeddedDatabaseConnection connection;
//
//    DataSourceBeanCreationException(String message, MybatisProperties2222222222 properties,
//                                    EmbeddedDatabaseConnection connection) {
//      super(message);
//      this.properties = properties;
//      this.connection = connection;
//    }
//
//    public MybatisProperties2222222222 getProperties() {
//      return this.properties;
//    }
//
//    public EmbeddedDatabaseConnection getConnection() {
//      return this.connection;
//    }
//
//  }
//
//  /**
//   * Initialize a {@link DataSourceBuilder} with the state of this instance.
//   * @return a {@link DataSourceBuilder} initialized with the customizations defined on
//   * this instance
//   */
//  public DataSourceBuilder<?> initializeDataSourceBuilder() {
//    return DataSourceBuilder.create(getClassLoader()).type(getType())
//            .driverClassName(determineDriverClassName()).url(determineUrl())
//            .username(determineUsername()).password(determinePassword());
//  }
//
//
//  public String determineDriverClassName() {
//    if (StringUtils.hasText(this.driverClassName)) {
//      Assert.state(driverClassIsLoadable(),
//              () -> "Cannot load driver class: " + this.driverClassName);
//      return this.driverClassName;
//    }
//    String driverClassName = null;
//    if (StringUtils.hasText(this.url)) {
//      driverClassName = DatabaseDriver.fromJdbcUrl(this.url).getDriverClassName();
//    }
//    if (!StringUtils.hasText(driverClassName)) {
//      driverClassName = this.embeddedDatabaseConnection.getDriverClassName();
//    }
//    if (!StringUtils.hasText(driverClassName)) {
//      throw new MybatisProperties2222222222.DataSourceBeanCreationException(
//              "Failed to determine a suitable driver class", this,
//              this.embeddedDatabaseConnection);
//    }
//    return driverClassName;
//  }
//
//  public String determineUsername() {
//    if (StringUtils.hasText(this.username)) {
//      return this.username;
//    }
//    if (EmbeddedDatabaseConnection.isEmbedded(determineDriverClassName())) {
//      return "sa";
//    }
//    return null;
//  }
//  /**
//   * Determine the password to use based on this configuration and the environment.
//   * @return the password to use
//   * @since 1.4.0
//   */
//  public String determinePassword() {
//    if (StringUtils.hasText(this.password)) {
//      return this.password;
//    }
//    if (EmbeddedDatabaseConnection.isEmbedded(determineDriverClassName())) {
//      return "";
//    }
//    return null;
//  }
//
//  private boolean driverClassIsLoadable() {
//    try {
//      ClassUtils.forName(this.driverClassName, null);
//      return true;
//    }
//    catch (UnsupportedClassVersionError ex) {
//      // Driver library has been compiled with a later JDK, propagate error
//      throw ex;
//    }
//    catch (Throwable ex) {
//      return false;
//    }
//  }

  @Override
  public String toString() {
    return "MybatisProperties{" +
            "classLoader=" + classLoader +
            ", url='" + url + '\'' +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", type=" + type +
            ", driverClassName='" + driverClassName + '\'' +
            ", name='" + name + '\'' +
            ", generateUniqueName=" + generateUniqueName +
            ", uniqueName='" + uniqueName + '\'' +
            ", embeddedDatabaseConnection=" + embeddedDatabaseConnection +
            ", configLocation='" + configLocation + '\'' +
            ", mapperLocations=" + Arrays.toString(mapperLocations) +
            ", typeAliasesPackage='" + typeAliasesPackage + '\'' +
            ", typeAliasesSuperType=" + typeAliasesSuperType +
            ", typeHandlersPackage='" + typeHandlersPackage + '\'' +
            ", checkConfigLocation=" + checkConfigLocation +
            ", executorType=" + executorType +
            ", configurationProperties=" + configurationProperties +
            ", configuration=" + configuration +
            '}';
  }
}
