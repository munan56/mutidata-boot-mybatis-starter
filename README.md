# mutidata-boot-mybatis-starter

a BOOT stater for mybatis with multiple datasource
```
 <dependency>
  <groupId>io.github.munan56</groupId>
  <artifactId>mybatis-spring-boot-starter</artifactId>
  <version>0.0.1</version>
</dependency>

```
```
multiple:
  mybatis:
    aaa:
      url: jdbc:mysql://localhost:3306/user?serverTimezone=UTC
      username: root
      password: admin123
      type: com.zaxxer.hikari.HikariDataSource
      driverClassName: com.mysql.cj.jdbc.Driver
      mapperScanPackage: 
      - com.github.munan56.boot.web.springwebdemo.mapper
    ccc:
      url: jdbc:mysql://localhost:3306/item?serverTimezone=UTC
      username: root
      password: admin123
      type: com.zaxxer.hikari.HikariDataSource
      driverClassName: com.mysql.cj.jdbc.Driver
      mapperScanPackage: 
      - com.github.munan56.boot.web.springwebdemo.a
```
