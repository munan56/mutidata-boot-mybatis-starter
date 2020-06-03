mine:
  configs:
    aaa:
      url: jdbc:mysql://localhost:3306/user?serverTimezone=UTC
      username: root
      password: admin123
      type: com.zaxxer.hikari.HikariDataSource
      driverClassName: com.mysql.cj.jdbc.Driver
      mapperScanPackage: com.github.munan56.boot.web.springwebdemo.mapper
    ccc:
      url: jdbc:mysql://localhost:3306/item?serverTimezone=UTC
      username: root
      password: admin123
      type: com.zaxxer.hikari.HikariDataSource
      driverClassName: com.mysql.cj.jdbc.Driver
      mapperScanPackage: com.github.munan56.boot.web.springwebdemo.a