

# mutidata-boot-mybatis-starter

a BOOT stater for mybatis with multiple datasource
```
 <dependency>
  <groupId>io.github.munan56</groupId>
  <artifactId>mybatis-spring-boot-starter</artifactId>
  <version>1.0.0</version>
</dependency>

```
if you have multiple data ready to config, you should config like this:

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

if you have multiple data ready to config, you should config like this:

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

if you have multiple data ready to config, you should config like this:

```yml
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



if you have single datasouce to config ,you should config like this:

```yml
  mybatis:
    url: jdbc:mysql://localhost:3306/user?serverTimezone=UTC
    username: root
    password: admin123
    type: com.zaxxer.hikari.HikariDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    mapperScanPackage: com.github.munan56.boot.web.springwebdemo.mapper
```

